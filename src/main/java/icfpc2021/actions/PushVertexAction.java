package icfpc2021.actions;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;

import java.util.*;
import java.util.function.Function;

public class PushVertexAction implements Action {
    public int vertex;
    public int dX;
    public int dY;

    public PushVertexAction(int vertex, int dX, int dY) {
        this.vertex = vertex;
        this.dX = dX;
        this.dY = dY;
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

    @Override
    public Figure apply(Figure figure) {
        if (dX == 0 && dY == 0) {
            // Nothing changed
            return figure;
        }

        // Calculate target invariant
        final long[] targetEdgeSquareLengths = squareEdgeLengthsFrom(figure.vertices, figure.edges);

        // Allocate mutable vertices and replace the touched vertex
        final List<Vertex> vertices = new ArrayList<>(figure.vertices);
        // Now the target is violated
        vertices.set(vertex, figure.vertices.get(vertex).move(dX, dY));

        // Try BFS traversal and move the vertices until the target is reached
        bfsExceptStart(figure.edges, vertex, integer -> {
            throw new UnsupportedOperationException("Implement me!");

        });

        // TODO: Keep away from the hole border

        return new Figure(vertices, figure.edges);
    }

    private void bfsExceptStart(
            List<Edge> edges,
            int startVertex,
            Function<Integer, Boolean> processVertexAndContinue) {
        Set<Integer> visited = new HashSet<>();
        // Do not process start vertex
        visited.add(startVertex);

        Deque<Integer> queue = new LinkedList<>();
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();

            // Expand neighbours
            edges.forEach(edge -> {
                if (edge.start == vertex) {
                    queue.addLast(edge.end);
                } else if (edge.end == vertex) {
                    queue.addLast(edge.start);
                }
            });

            // Skip visited
            if (visited.contains(vertex)) {
                continue;
            }

            // Process
            if (processVertexAndContinue.apply(vertex)) {
                // And mark visited
                visited.add(vertex);
            } else {
                // The goal is reached
                return;
            }
        }
    }
}
