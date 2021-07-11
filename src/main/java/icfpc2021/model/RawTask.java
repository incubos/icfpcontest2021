package icfpc2021.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RawTask {
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
    static class Bonus {
        public List<Integer> position;
        public String bonus;
        public Integer problem;
    }
    public List<List<Integer>> hole;
    public Figure figure;
    public int epsilon;
    public List<Bonus> bonuses;

    @JsonCreator
    public RawTask(@JsonProperty("hole") List<List<Integer>> hole,
                   @JsonProperty("figure") Figure figure) {
        this.hole = hole;
        this.figure = figure;
    }
}
