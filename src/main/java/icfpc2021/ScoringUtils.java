package icfpc2021;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.List;

public class ScoringUtils {
    /**
     * Returns true if the figure completely fits with the hole.
     */
    public static boolean fitsWithinHole(Figure figure, Hole hole) {
        var figurePath = verticesToPath(figure.vertices);
        var holePath = verticesToPath(hole.vertices);
        var area = new Area(figurePath);
        area.subtract(new Area(holePath));
        return area.isEmpty();
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
        final double[] originalEdgesLength = edgeLengthsFrom(originalFigure.vertices, originalFigure.edges);
        final double[] edgesLength = edgeLengthsFrom(figure.vertices, figure.edges);
        double absDiffSum = absDiffSum(originalEdgesLength, edgesLength);
        return absDiffSum <= epsilon;
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

    public static double maxDiffRatio(double[] a, double[] b) {
        double result = 0.0;
        for (int i = 0; i < a.length; i++) {
            assert a[i] > 0.0;
            assert b[i] > 0.0;
            result = Math.max(result, Math.abs(a[i] / b[i] - 1.0));
        }
        return result;
    }
}
