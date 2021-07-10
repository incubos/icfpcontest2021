package icfpc2021;

import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.List;

import static icfpc2021.actions.PushVertexAction.absDiffSum;
import static icfpc2021.actions.PushVertexAction.edgeLengthsFrom;

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

}
