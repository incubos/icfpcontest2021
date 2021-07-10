package icfpc2021;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static icfpc2021.ConvexHullKt.counterClockWise;

public class ScoringUtils {
    private static final double COORDINATE_PRECISION = 0.001;

    /**
     * Returns true if the figure completely fits with the hole.
     */
    public static boolean fitsWithinHole(Figure figure, Hole hole) {
        var figurePath = verticesToPath(figure.vertices);
        var area = new Area(figurePath);
        if (!area.isEmpty()) {
            var holePath = verticesToPath(hole.vertices);
            area.subtract(new Area(holePath));
            return area.isEmpty();
        } else {
            // Check edges intersections
            for (Edge edge : figure.edges) {
                var e1 = figure.vertices.get(edge.start);
                var e2 = figure.vertices.get(edge.end);
                for (int i = 0; i < hole.vertices.size(); i++) {
                    var h1 = hole.vertices.get(i);
                    var h2 = hole.vertices.get((i + 1) % hole.vertices.size());
                    if (e1.equals(h1) || e2.equals(h1) || e1.equals(h2) || e2.equals(h2)) {
                        continue; // Precise fit
                    }
                    if (intersects(e1, e2, h1, h2)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static boolean intersects(Vertex e1, Vertex e2, Vertex h1, Vertex h2) {
        return counterClockWise(e1, e2, h1) != counterClockWise(e1, e2, h2) &&
                counterClockWise(h1, h2, e1) != counterClockWise(h1, h2, e2);
    }

    public static List<Integer> listNotFitting(Figure figure, Hole hole) {
        var holePath = verticesToPath(hole.vertices);
        var area = new Area(holePath);
        ArrayList<Integer> notFitting = new ArrayList<>();
        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (!area.contains(vertex.x, vertex.y)) {
                notFitting.add(i);
            }
        }
        return notFitting;
    }

    private static Path2D verticesToPath(List<Vertex> vertices) {
        var path = new Path2D.Double();
        var firstVertex = vertices.get(0);
        path.moveTo(firstVertex.x, firstVertex.y);
        for (int i = 1; i < vertices.size(); i++) {
            var vertex = vertices.get(i);
            path.lineTo(vertex.x, vertex.y);
        }
        path.closePath();
        return path;
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
        // 0 1
        //  v
        // 3 2

        {
            // 0
            Vertex candidate = new Vertex(Math.floor(vertex.x), Math.floor(vertex.y));
            double score = maxEpsilonIfReplace(i, candidate, vertices, edges, originalFigure);
            if (bestEpsilon > score) {
                bestEpsilon = score;
                bestCandidate = candidate;
            }
        }

        {
            // 1
            Vertex candidate = new Vertex(Math.ceil(vertex.x), Math.floor(vertex.y));
            double score = maxEpsilonIfReplace(i, candidate, vertices, edges, originalFigure);
            if (bestEpsilon > score) {
                bestEpsilon = score;
                bestCandidate = candidate;
            }
        }

        {
            // 2
            Vertex candidate = new Vertex(Math.ceil(vertex.x), Math.ceil(vertex.y));
            double score = maxEpsilonIfReplace(i, candidate, vertices, edges, originalFigure);
            if (bestEpsilon > score) {
                bestEpsilon = score;
                bestCandidate = candidate;
            }
        }

        {
            // 3
            Vertex candidate = new Vertex(Math.floor(vertex.x), Math.ceil(vertex.y));
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
}
