package icfpc2021.model;

import java.util.List;
import java.util.Objects;

public class Figure {
    public List<Vertex> vertices;
    public List<Edge> edges;

    public Figure(List<Vertex> vertices, List<Edge> edges) {
        this.vertices = vertices;
        this.edges = edges;
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
}
