package icfpc2021.actions;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FoldAction implements Action {

    public static final ArrayList<Integer> EMPTY_ARRAY = new ArrayList<>();

    static class Axis {
        final Vertex start;
        final Vertex end;

        double x0;

        double x1;
        double y0;
        double y1;
        // y = ax + b;
        double a;
        double b;

        public Axis(Vertex start, Vertex end) {
            this.start = start;
            this.end = end;
            x0 = start.x;
            x1 = end.x;
            y0 = start.y;
            y1 = end.y;
            a = (y0 - y1) / (x0 - x1);
            b = y0 - a * x0;
        }
    }

    public int vertex1;
    public int vertex2;
    public int subFigureVertex;

    // FoldAction is defined by three points: two defines an axis to pivot the folding, third defines a subfigure to fold.
    // e.g.
    //     0   1       2
    //     *---*-------*
    //     |   |       |
    //     |   |3      |4
    //     |   *-------*
    //     |  /
    //     | /
    //     |/
    //     *5
    // Folding 0 into axis (1,3) will result in following
    //         1   0   2
    //         *---*---*
    //         |   |   |
    //         |3  |   |4
    //         *---|---*
    //          \  |
    //           \ |
    //            \|
    //             *5

    public FoldAction(int vertex1, int vertex2, int subFigureVertex) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
        this.subFigureVertex = subFigureVertex;
    }

    private Vertex fold(Vertex vertex, Axis axis) {
        //  *--*
        //  |  |
        //  *--*   <-- Axis
        if (axis.start.y == axis.end.y) {
            return new Vertex(vertex.x, vertex.y - 2 * (vertex.y - axis.start.y));
        }
        //  *--*
        //  |  |
        //  *--*
        //     ^
        //     |
        //    axis
        if (axis.start.x == axis.end.x) {
            return new Vertex(vertex.x - 2 * (vertex.x - axis.start.x), vertex.y);
        }

        double a = axis.a;
        double b = axis.b;

        // orthogonal line coefficient = -1/a
        double aPrime = -1 / a;

        // orthogonal line function vertex.y = aPrime * vertex.x + bPrime; bPrime = vertex.y - aPrime * vertex.x
        double bPrime = vertex.y - aPrime * vertex.x;

        // find the intersection
        // aPrime * xIntersect + bPrime = a * xIntersect + b; xIntersect = (bPrime - b) / (a - aPrime)
        double xIntersect = (bPrime - b) / (a - aPrime);
        // yIntersect = a * xIntersect + b;
        double yIntersect = a * xIntersect + b;

        // continue the line to find a mirrored point
        return new Vertex(vertex.x - 2 * (vertex.x - xIntersect), vertex.y - 2 * (vertex.y - yIntersect));
    }

    @Override
    public Figure apply(State state, Figure figure) {
        if (vertex1 == vertex2) {
            // invalid action
            return figure;
        }
        Axis axis = new Axis(figure.vertices.get(vertex1), figure.vertices.get(vertex2));

        Set<Integer> excludedNodes = new HashSet<>();
        excludedNodes.add(vertex1);
        excludedNodes.add(vertex2);
        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (axis.a * vertex.x + axis.b == vertex.y) {
                excludedNodes.add(i);
            }
        }

        if (!canApply(state.getAdjacencyList(), excludedNodes, figure.vertices.size() - excludedNodes.size(), subFigureVertex)) {
            return figure;
        }

        final List<Vertex> vertices = new ArrayList<>(figure.vertices);
        bfsSubFigure(state.getAdjacencyList(), vertices, axis, subFigureVertex, excludedNodes);
        return new Figure(vertices, figure.edges);
    }

    private boolean canApply(
            Map<Integer, List<Integer>> edges,
            Set<Integer> excludedNodes,
            int numVertices,
            int startVertex) {
        Set<Integer> visited = new HashSet<>();

        Deque<Integer> queue = new LinkedList<>();
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            // Expand neighbours
            edges.getOrDefault(vertex, EMPTY_ARRAY).forEach(edge -> {
                if (!excludedNodes.contains(edge) && !visited.contains(edge)) {
                    queue.addLast(edge);
                }
            });

            // Skip visited
            if (visited.contains(vertex)) {
                continue;
            }

            visited.add(vertex);
        }
        return visited.size() < numVertices;
    }

    private void bfsSubFigure(
            Map<Integer, List<Integer>> edges,
            List<Vertex> vertices,
            Axis axis,
            int startVertex,
            Set<Integer> excludedNodes) {
        Set<Integer> visited = new HashSet<>();

        Deque<Integer> queue = new LinkedList<>();
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            if (excludedNodes.contains(vertex)) {
                continue;
            }

            // Expand neighbours
            edges.getOrDefault(vertex, EMPTY_ARRAY).forEach(edge -> {
                if (!excludedNodes.contains(edge) && !visited.contains(edge)) {
                    queue.addLast(edge);
                }
            });

            // Skip visited
            if (visited.contains(vertex)) {
                continue;
            }

            // Process
            vertices.set(vertex, fold(vertices.get(vertex), axis));
            visited.add(vertex);
        }

        //throw new IllegalStateException("Can't reach the goal");
    }

    /**
     * If all the vertices are accessible from subFigureVertex while removing v1 and v2, then incorrect.
     */
    public static boolean checkCorrect(int vertex1, int vertex2, int subFigureVertex, List<Edge> edges) {
        if (vertex1 == vertex2 || vertex1 == subFigureVertex || vertex2 == subFigureVertex) {
            return false;
        }
        Set<Integer> visited = new HashSet<>();
        Deque<Integer> queue = new LinkedList<>();
        queue.add(subFigureVertex);
        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            // Skip visited
            if (visited.contains(vertex)) {
                continue;
            } else {
                visited.add(vertex);
            }
            if (vertex == vertex1 || vertex == vertex2) {
                continue;
            }

            // Expand neighbours
            edges.forEach(edge -> {
                if (edge.start == vertex && !visited.contains(edge.end) &&
                        edge.end != vertex1 && edge.end != vertex2) {
                    queue.addLast(edge.end);
                } else if (edge.end == vertex && !visited.contains(edge.start) &&
                        edge.start != vertex1 && edge.start != vertex2) {
                    queue.addLast(edge.start);
                }
            });
        }
        // Check that not everything is accessible
        return visited.size() < edges.size() - 2;
    }
}
