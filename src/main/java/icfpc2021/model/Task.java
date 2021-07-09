package icfpc2021.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Task {
    static class Figure {
        public List<List<Integer>> edges;
        public List<List<Integer>> vertices;

        @JsonCreator
        public Figure(@JsonProperty("edges") List<List<Integer>> edges,
                      @JsonProperty("vertices") List<List<Integer>> vertices) {
            this.edges = edges;
            this.vertices = vertices;
        }
    }
    public List<List<Integer>> hole;
    public Figure figure;
    public int epsilon;

    @JsonCreator
    public Task(@JsonProperty("hole") List<List<Integer>> hole,
                @JsonProperty("figure") Figure figure) {
        this.hole = hole;
        this.figure = figure;
    }
}
