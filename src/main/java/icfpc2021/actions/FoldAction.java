package icfpc2021.actions;

import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;

import java.util.*;
import java.util.function.Function;

public class FoldAction implements Action {
    static class Axis {
        Vertex start;
        Vertex end;

        public Axis(Vertex start, Vertex end) {
            this.start = start;
            this.end = end;
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

        double x0 = axis.start.x;
        double x1 = axis.end.x;
        double y0 = axis.start.y;
        double y1 = axis.end.y;
        // y = ax + b;
        double a = (y0 - y1) / (x0 - x1);
        double b = y0 - a * x0;

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
    public Figure apply(Figure figure) {
        if (vertex1 == vertex2) {
            // invalid action
            return figure;
        }

        // can only flip around an edge
        if (figure.edges.stream().noneMatch(o -> (o.start == vertex1 && o.end == vertex2) ||
                (o.start == vertex2 && o.end == vertex1))) {
            return figure;
        }
        final List<Vertex> vertices = new ArrayList<>(figure.vertices);
        bfsSubFigure(figure.edges, vertices, subFigureVertex, vertex1, vertex2);
        return new Figure(vertices, figure.edges);

    }

    private void bfsSubFigure(
            List<Edge> edges,
            List<Vertex> vertices,
            int startVertex,
            int endVertex1,
            int endVertex2) {
        Set<Integer> visited = new HashSet<>();

        Deque<Integer> queue = new LinkedList<>();
        queue.add(startVertex);
        while (!queue.isEmpty()) {
            int vertex = queue.removeFirst();
            if (endVertex1 == vertex || endVertex2 == vertex) {
                continue;
            }

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
            System.out.println(vertex);
            vertices.set(vertex, fold(vertices.get(vertex), new Axis(vertices.get(vertex1), vertices.get(vertex2))));
            visited.add(vertex);
        }

        //throw new IllegalStateException("Can't reach the goal");
    }

}
