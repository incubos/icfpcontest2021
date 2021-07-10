package icfpc2021.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.DoubleStream;

public class Figure {
    public final List<Vertex> vertices;
    public final List<Edge> edges;

    public Figure(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = List.copyOf(vertices);
        this.edges = List.copyOf(edges);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Figure figure = (Figure) o;
        return Objects.equals(vertices, figure.vertices) && Objects.equals(edges, figure.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }

    @Override
    public String toString() {
        return "Figure{" +
                "vertices=" + vertices +
                ", edges=" + edges +
                '}';
    }

    public double[] verticesToDoubleArray() {
        return vertices
                .stream()
                .flatMapToDouble(v -> DoubleStream.of(v.x, v.y))
                .toArray();
    }

    public Figure copyVerticesFromDoubleArray(double[] array) {
        var result = new ArrayList<Vertex>();
        for (int i = 0; i < array.length; i += 2) {
            result.add(new Vertex(array[i], array[i + 1]));
        }
        return new Figure(result, edges);
    }

    public double[] center() {
        return new double[] {
                vertices.stream().mapToDouble(v -> v.x).average().getAsDouble(),
                vertices.stream().mapToDouble(v -> v.y).average().getAsDouble()
        };
    }
}
