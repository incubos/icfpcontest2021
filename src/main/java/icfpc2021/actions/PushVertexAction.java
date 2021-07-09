package icfpc2021.actions;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;

import java.util.ArrayList;
import java.util.List;

public class PushVertexAction implements Action {
    public int vertex;
    public int dX;
    public int dY;

    public PushVertexAction(int vertex, int dX, int dY) {
        this.vertex = vertex;
        this.dX = dX;
        this.dY = dY;
    }

    @Override
    public Figure apply(Figure figure) {
        // Target invariant
        long[] targetEdgeSquareLengths = squareEdgeLengthsFrom(figure.vertices, figure.edges);

        // Allocate mutable vertices and replace the touched vertex
        List<Vertex> vertices = new ArrayList<>(figure.vertices);
        vertices.set(vertex, figure.vertices.get(vertex).move(dX, dY));

        // TODO: Try to move adjacent vertices until the target is reached
        //???

        // TODO: Stretch the graph
        // TODO: Keep away from the hole border

        return new Figure(vertices, figure.edges);
    }

    private static long[] squareEdgeLengthsFrom(
            List<Vertex> vertices,
            List<Edge> edges) {
        long[] result = new long[edges.size()];
        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            Vertex start = vertices.get(edge.start);
            Vertex end = vertices.get(edge.end);
            long dx = (long) (end.x - start.x);
            long dy = (long) (end.y - start.y);
            result[i] = dx * dx + dy * dy;
        }
        return result;
    }
}
