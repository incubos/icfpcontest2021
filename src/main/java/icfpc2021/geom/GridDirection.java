package icfpc2021.geom;

import icfpc2021.model.Vertex;

public enum GridDirection {
    TOP_LEFT(0),
    TOP_RIGHT(1),
    BOTTOM_LEFT(2),
    BOTTOM_RIGHT(3);

    int code;

    GridDirection(int code) {
        this.code = code;
    }

    public Vertex move(Vertex vertex) {
        if (code == 0) {
            return new Vertex(Math.floor(vertex.x), Math.floor(vertex.y));
        }
        if (code == 1) {
            return new Vertex(Math.ceil(vertex.x), Math.floor(vertex.y));
        }
        if (code == 2) {
            return new Vertex(Math.floor(vertex.x), Math.ceil(vertex.y));
        }
        if (code == 3) {
            return new Vertex(Math.ceil(vertex.x), Math.ceil(vertex.y));
        }
        throw new IllegalStateException("WAT?");
    }

}
