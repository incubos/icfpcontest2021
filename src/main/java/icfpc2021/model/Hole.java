package icfpc2021.model;

import java.util.List;

public class Hole {
    public List<Vertex> vertices;

    public Hole(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public double[] center() {
        return new double[] {
                vertices.stream().mapToDouble(v -> v.x).average().getAsDouble(),
                vertices.stream().mapToDouble(v -> v.y).average().getAsDouble()
        };
    }

}
