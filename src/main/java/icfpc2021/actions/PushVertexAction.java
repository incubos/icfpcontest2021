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

    private static double[] edgeLengthsFrom(
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

    @Override
    public Figure apply(Figure figure) {
        if (dX == 0 && dY == 0) {
            // Nothing changed
            return figure;
        }

        // Calculate target invariant
        final double[] targetEdgeSquareLengths = edgeLengthsFrom(figure.vertices, figure.edges);

        // Allocate mutable vertices and replace the touched vertex
        final List<Vertex> vertices = new ArrayList<>(figure.vertices);
        // Now the target is violated
        vertices.set(vertex, figure.vertices.get(vertex).move(dX, dY));

        // Try BFS traversal and move the vertices until the target is reached
        bfsExceptStart(
                figure.edges,
                vertex,
                current -> moveToLocalOptimumAndReturnDiff(figure.edges, vertices, current, targetEdgeSquareLengths) > 0.001);

        return new Figure(vertices, figure.edges);
    }

    private static double moveToLocalOptimumAndReturnDiff(
            List<Edge> edges,
            List<Vertex> vertices,
            int vertex,
            double[] goal) {
        // TODO: Keep away from the hole border

        // Zero means the goal is reached
        double best = absDiffSum(goal, edgeLengthsFrom(vertices, edges));

        // Try to move each direction for many times
        boolean advanced;
        do {
            advanced = false;
            for (final Direction direction : Direction.values()) {
                double estimate;
                while (best > 0.001) {
                    // Remember to be able to rollback
                    Vertex v = vertices.get(vertex);

                    // Move
                    vertices.set(vertex, v.move(direction.dx, direction.dy));
                    estimate = absDiffSum(goal, edgeLengthsFrom(vertices, edges));
                    if (estimate >= best) {
                        // Rollback
                        vertices.set(vertex, v);
                        break;
                    } else {
                        // Move on
                        best = estimate;
                        advanced = true;
                    }
                }
            }
        } while (advanced);

        return best;
    }

    private static double absDiffSum(double[] a, double[] b) {
        double result = 0L;
        for (int i = 0; i < a.length; i++) {
            final double diff = a[i] - b[i];
            result += Math.abs(diff);
        }
        return result;
    }

    enum Direction {
        UP(0, -1),
        RIGHT(1, 0),
        UP_RIGHT(1, -1),
        DOWN(0, 1),
        DOWN_RIGHT(1, 1),
        LEFT(-1, 0),
        UP_LEFT(-1, -1),
        DOWN_LEFT(-1, 1);

        final int dx, dy;

        Direction(final int dx, final int dy) {
            this.dx = dx;
            this.dy = dy;
        }
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
                if (edge.start == vertex && !visited.contains(edge.end)) {
                    queue.addLast(edge.end);
                } else if (edge.end == vertex && !visited.contains(edge.start)) {
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

        //throw new IllegalStateException("Can't reach the goal");
    }
}
