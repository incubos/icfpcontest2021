package icfpc2021.model;

public class Vertex {
    public double x;
    public double y;
    public Vertex(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vertex vertex = (Vertex) o;
        return Math.round(vertex.x) == Math.round(x) &&
                Math.round(vertex.y) == Math.round(y);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
