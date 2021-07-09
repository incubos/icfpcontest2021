package icfpc2021.model;

import java.util.Objects;

public class Edge {
    public int start;

    public Edge(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public int end;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return start == edge.start && end == edge.end;
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
