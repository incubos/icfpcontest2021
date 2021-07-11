package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class MoveVertexToGridAction implements Action {
    private final int v;
    private final Vertex gridVertex;

    public MoveVertexToGridAction(int v, int x, int y) {
        this.v = v;
        this.gridVertex = new Vertex(x, y);
    }
    public MoveVertexToGridAction(int v, Vertex gridVertex) {
        this.v = v;
        this.gridVertex = gridVertex;
    }


    @Override
    public Figure apply(State state, Figure figure) {
        List<Vertex> vertices = new ArrayList<>(figure.vertices);
        vertices.set(v, gridVertex);
        return new Figure(vertices, figure.edges);
    }

    @Override
    public String toString() {
        return "MoveVertexToGrid " + v +  ": " + gridVertex.x + "," + gridVertex.y;
    }
}
