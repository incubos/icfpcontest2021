package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.*;
import java.util.function.Function;

public class PushVertexAction implements Action {
    private static final double ACCEPTABLE_SUM_LENGTH_DIFF = 0.01;

    public int vertex;
    public int targetX;
    public int targetY;

    public PushVertexAction(int vertex, int targetX, int targetY) {
        this.vertex = vertex;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    private static double moveToLocalOptimumAndReturnDiff(
            List<Edge> edges,
            List<Vertex> vertices,
            int vertex,
            double[] goal) {
        // TODO: Keep away from the hole border

        // Zero means the goal is reached
        double best = ScoringUtils.absDiffSum(goal, ScoringUtils.edgeLengthsFrom(vertices, edges));

        // Try to move each direction for many times
        boolean advanced;
        do {
            advanced = false;
            for (final Direction direction : Direction.values()) {
                double estimate;
                while (best > ACCEPTABLE_SUM_LENGTH_DIFF) {
                    // Remember to be able to rollback
                    Vertex v = vertices.get(vertex);

                    // Move
                    vertices.set(vertex, v.move(direction.dx, direction.dy));
                    estimate = ScoringUtils.absDiffSum(goal, ScoringUtils.edgeLengthsFrom(vertices, edges));
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

    @Override
    public Figure apply(State state, Figure figure) {
        // Calculate target invariant
        final double[] targetEdgeSquareLengths = ScoringUtils.edgeLengthsFrom(figure.vertices, figure.edges);

        // Allocate mutable vertices and replace the touched vertex
        final List<Vertex> vertices = new ArrayList<>(figure.vertices);
        // Now the target is violated
        vertices.set(vertex, new Vertex(targetX, targetY));

        // Try BFS traversal and move the vertices until the target is reached
        try {
            bfsExceptStart(
                    figure.edges,
                    vertex,
                    current -> moveToLocalOptimumAndReturnDiff(figure.edges, vertices, current, targetEdgeSquareLengths) > ACCEPTABLE_SUM_LENGTH_DIFF);
            return new Figure(vertices, figure.edges);
        } catch (IllegalStateException e) {
            System.err.println("Can't reach the goal. Rolling back.");
            return figure;
        }
    }

    private void bfsExceptStart(
            List<Edge> edges,
            int startVertex,
            Function<Integer, Boolean> processVertexAndContinue) throws IllegalStateException {
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

        throw new IllegalStateException("Can't reach the goal");
    }

    @Override
    public String toString() {
        return "PushVertex[" + vertex + "," + targetX + "," + targetY + "]";
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
}
