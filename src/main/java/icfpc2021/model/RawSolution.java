package icfpc2021.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RawSolution {
    public List<List<Integer>> vertices;

    @JsonCreator
    public RawSolution(@JsonProperty("vertices") List<List<Integer>> vertices) {
        this.vertices = vertices;
    }
}