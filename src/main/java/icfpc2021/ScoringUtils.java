package icfpc2021;

import icfpc2021.geom.GridDirection;
import icfpc2021.geom.Triangle;
import icfpc2021.geom.Triangulate;
import icfpc2021.geom.TriangulateKt;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static icfpc2021.ConvexHullKt.convexHull;

public class ScoringUtils {
    private static final double COORDINATE_PRECISION = 0.001;
    public static final Pair<GridDirection, GridDirection> STAY_IN_GREED =
            Pair.create(GridDirection.IN_GRID, GridDirection.IN_GRID);

    /**
     * Returns true if the figure completely fits with the hole.
     */
    public static boolean fitsWithinHole(Figure figure, Hole hole) {
        // Check all vertices in hole
        if (!listNotFitting(figure, hole).isEmpty()) {
            return false;
        }
        // Check holes in hole intersection
        List<Triangle> holesInHoleTriangles = TriangulateKt.triangulateHolesInHole(hole, convexHull(hole.vertices));
        if (figure.edges.stream().anyMatch(e -> {
            Vertex x1 = figure.vertices.get(e.start);
            var x1x = x1.x;
            var x1y = x1.y;
            Vertex x2 = figure.vertices.get(e.end);
            var x2x = x2.x;
            var x2y = x2.y;

            return holesInHoleTriangles.stream().anyMatch(t -> {
                        var a = hole.vertices.get(t.getA());
                        var b = hole.vertices.get(t.getB());
                        var c = hole.vertices.get(t.getC());
                        var ax = a.x;
                        var ay = a.y;
                        var bx = b.x;
                        var by = b.y;
                        var cx = c.x;
                        var cy = c.y;
                        boolean intersectsTriangle = edgeIntersectsTriangle(ax, ay, bx, by, cx, cy, x1x, x1y, x2x, x2y);
//                        if (intersectsTriangle) {
//                            System.out.println("Figure edge " + e.start + "(" + x1x + ", " + x1y + "), " +
//                                    e.end + "(" + x2x + ", " + x2y + ") " +
//                                    "intersects with outer boundary " +
//                                    t.getA() + "(" + ax + ", " + bx + "), " +
//                                    t.getB() + "(" + bx + ", " + bx + "), " +
//                                    t.getC() + "(" + cx + ", " + cx + ")");
//                        }
                        return intersectsTriangle;
                    }
            );
        })) {
            return false;
        }
        return true;
    }

    /**
     * The same formula as in header.smt
     */
    public static boolean edgeIntersectsTriangle(double ax, double ay,
                                                 double bx, double by,
                                                 double cx, double cy,
                                                 double x1x, double x1y,
                                                 double x2x, double x2y) {
//        return
//                direction(ax, ay, bx, by, x1x, x1y) < -EPSILON ^ direction(ax, ay, bx, by, x2x, x2y) < -EPSILON &&
//                        direction(x1x, x1y, x2x, x2y, ax, ay) < -EPSILON ^ direction(x1x, x1y, x2x, x2y, bx, by) < -EPSILON
//                        ||
//                        direction(bx, by, cx, cy, x1x, x1y) < -EPSILON ^ direction(bx, by, cx, cy, x2x, x2y) < -EPSILON &&
//                                direction(x1x, x1y, x2x, x2y, bx, by) < -EPSILON ^ direction(x1x, x1y, x2x, x2y, cx, cy) < -EPSILON
//                        ||
//                        direction(cx, cy, ax, ay, x1x, x1y) < -EPSILON ^ direction(cx, cy, ax, ay, x2x, x2y) < -EPSILON &&
//                                direction(x1x, x1y, x2x, x2y, cx, cy) < -EPSILON ^ direction(x1x, x1y, x2x, x2y, ax, ay) < -EPSILON;
        return
                direction(ax, ay, bx, by, x1x, x1y) * direction(ax, ay, bx, by, x2x, x2y) < 0 &&
                        direction(x1x, x1y, x2x, x2y, ax, ay) * direction(x1x, x1y, x2x, x2y, bx, by) < 0
                        ||
                        direction(bx, by, cx, cy, x1x, x1y) * direction(bx, by, cx, cy, x2x, x2y) < 0 &&
                                direction(x1x, x1y, x2x, x2y, bx, by) * direction(x1x, x1y, x2x, x2y, cx, cy) < 0
                        ||
                        direction(cx, cy, ax, ay, x1x, x1y) * direction(cx, cy, ax, ay, x2x, x2y) < 0 &&
                                direction(x1x, x1y, x2x, x2y, cx, cy) * direction(x1x, x1y, x2x, x2y, ax, ay) < 0;
    }

    private static double direction(double ax, double ay, double bx, double by, double cx, double cy) {
        return ax * by - ay * bx + ay * cx - ax * cy + bx * cy - cx * by;
    }

    public static List<Integer> listNotFitting(Figure figure, Hole hole) {
        List<Triangle> holesTriangles = Triangulate.Companion.triangulate(hole.vertices);
        return IntStream.range(0, figure.vertices.size()).filter(
                vI -> {
                    var v = figure.vertices.get(vI);
                    boolean found = holesTriangles.stream().anyMatch(
                            t -> {
                                Vertex a = hole.vertices.get(t.getA());
                                Vertex b = hole.vertices.get(t.getB());
                                Vertex c = hole.vertices.get(t.getC());
                                return Triangulate.Companion.pointInTriangle(v, a, b, c);
                            });
//                    if (!found) {
//                        System.out.println("Figure vertex " + figure.vertices.indexOf(v) +
//                                " (" + v.x + ", " + v.y + ") out of any hole triangles");
//                    }
                    return !found;
                }).boxed().collect(Collectors.toList());
    }

    /**
     * Returns true if the figure is transformed correctly
     */
    public static boolean checkFigure(Figure figure, Figure originalFigure, double epsilon) {
        final double[] originalEdgesLength = edgeSquareLengthsFrom(originalFigure.vertices, originalFigure.edges);
        final double[] edgesLength = edgeSquareLengthsFrom(figure.vertices, figure.edges);
        final double threshold = epsilon / 1_000_000.0;
        for (int i = 0; i < originalEdgesLength.length; i++) {
            final double score = Math.abs(edgesLength[i] / originalEdgesLength[i] - 1.0);
            if (score > threshold) {
                return false;
            }
        }
        return true;
    }

    public static boolean checkEdge(Figure figure, Figure originalFigure, double epsilon, int i) {
        final double[] originalEdgesLength = edgeSquareLengthsFrom(originalFigure.vertices, originalFigure.edges);
        final double[] edgesLength = edgeSquareLengthsFrom(figure.vertices, figure.edges);
        final double threshold = epsilon / 1_000_000.0;
        final double score = Math.abs(edgesLength[i] / originalEdgesLength[i] - 1.0);
        if (score > threshold) {
            return false;
        }
        return true;
    }


    public static double[] edgeLengthsFrom(
            List<Vertex> vertices,
            List<Edge> edges) {
        double[] result = new double[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            Vertex start = vertices.get(edge.start);
            Vertex end = vertices.get(edge.end);
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            result[i] = Math.hypot(dx, dy);
        }
        return result;
    }

    public static double[] edgeSquareLengthsFrom(
            List<Vertex> vertices,
            List<Edge> edges) {
        double[] result = new double[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            Vertex start = vertices.get(edge.start);
            Vertex end = vertices.get(edge.end);
            double dx = end.x - start.x;
            double dy = end.y - start.y;
            result[i] = dx * dx + dy * dy;
        }
        return result;
    }

    public static double absDiffSum(double[] a, double[] b) {
        double result = 0L;
        for (int i = 0; i < a.length; i++) {
            final double diff = a[i] - b[i];
            result += Math.abs(diff);
        }
        return result;
    }

    public static boolean isIntegerCoordinates(Vertex vertex) {
        return Math.abs(Math.round(vertex.x) - vertex.x) < COORDINATE_PRECISION
                && Math.abs(Math.round(vertex.y) - vertex.y) < COORDINATE_PRECISION;
    }

    public static Vertex round(
            int i,
            Vertex vertex,
            List<Vertex> vertices,
            List<Edge> edges,
            Figure originalFigure) {
        Vertex bestCandidate = vertex;
        double bestEpsilon = Double.MAX_VALUE;

        // Check all possible roundings from floor() to ceil()
        for (GridDirection gridDirection : GridDirection.variants(false)) {
            Vertex candidate = gridDirection.move(vertex);
            double score = maxEpsilonIfReplace(i, candidate, vertices, edges, originalFigure);
            if (bestEpsilon > score) {
                bestEpsilon = score;
                bestCandidate = candidate;
            }
        }
        return bestCandidate;
    }

    public static double maxEpsilonIfReplace(
            int vertex,
            Vertex to,
            List<Vertex> vertices,
            List<Edge> edges,
            Figure original) {
        double maxEpsilon = 0.0;
        for (final Edge edge : edges) {
            if (edge.start != vertex && edge.end != vertex) {
                // Skip
                continue;
            }

            final Vertex originalStart = original.vertices.get(edge.start);
            final Vertex originalEnd = original.vertices.get(edge.end);
            final double originalSquareLength = squareLength(originalStart, originalEnd);
            final double newSquareLength;
            if (edge.start == vertex) {
                newSquareLength = squareLength(to, vertices.get(edge.end));
            } else {
                newSquareLength = squareLength(vertices.get(edge.start), to);
            }

            final double epsilon = Math.abs(newSquareLength / originalSquareLength - 1.0);
            maxEpsilon = Math.max(maxEpsilon, epsilon);
        }

        return maxEpsilon;
    }

    private static double squareLength(Vertex start, Vertex end) {
        final double dx = end.x - start.x;
        final double dy = end.y - start.y;
        return dx * dx + dy * dy;
    }

    public static List<Pair<GridDirection, GridDirection>> getEdgeCorrectRounds(Vertex vStart, Vertex vEnd,
                                                                                double originalSquareLength, double threshold) {
        boolean nonGridStart = !ScoringUtils.isIntegerCoordinates(vStart);
        boolean nonGridEnd = !ScoringUtils.isIntegerCoordinates(vEnd);
        ArrayList<Pair<GridDirection, GridDirection>> variants = new ArrayList<>();
        if (nonGridStart || nonGridEnd) {
            // Iterate over decart product and check valid variants
            for (GridDirection startVariant : GridDirection.variants(!nonGridStart)) {
                var newStart = startVariant.move(vStart);
                for (GridDirection endVariant : GridDirection.variants(!nonGridEnd)) {
                    var newEnd = endVariant.move(vEnd);
                    if (Math.abs(squareLength(newStart, newEnd) / originalSquareLength - 1.0) < threshold) {
                        variants.add(Pair.create(startVariant, endVariant));
                    }
                }
            }
        } else {
            variants.add(STAY_IN_GREED);
        }
        return variants;
    }

    public static boolean isFigureInGrid(Figure figure) {
        for (Vertex vertex : figure.vertices) {
            if (!ScoringUtils.isIntegerCoordinates(vertex)) {
                return false;
            }
        }
        return true;
    }
}
