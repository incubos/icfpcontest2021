package icfpc2021.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.stream.Collectors;

public class Pose {
    @JsonProperty
    List<List<Long>> vertices;

    public Pose(List<List<Long>> vertices) {
        this.vertices = vertices;
    }

    public static Pose fromVertices(List<Vertex> vertices) {
        return new Pose(vertices.stream().map(o-> List.of(Math.round(o.x), Math.round(o.y))).collect(Collectors.toList()));
    }
}
