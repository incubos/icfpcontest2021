package icfpc2021.actions;

import icfpc2021.geom.GridDirection;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.apache.commons.math3.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class MoveEdgeToGridAction implements Action {
    private final Pair<GridDirection, GridDirection> fix;
    private final int start;
    private final int end;

    public MoveEdgeToGridAction(Pair<GridDirection, GridDirection> fix, int start, int end) {
        this.fix = fix;
        this.start = start;
        this.end = end;
    }

    @Override
    public Figure apply(State state, Figure figure) {
        List<Vertex> vertices = new ArrayList<>(figure.vertices);
        vertices.set(start, fix.getFirst().move(figure.vertices.get(start)));
        vertices.set(end, fix.getSecond().move(figure.vertices.get(end)));
        return new Figure(vertices, figure.edges);
    }

    @Override
    public String toString() {
        return "MoveEdgeToGrid " + start + "," + end +  "->" + fix.getFirst() + "," + fix.getSecond();
    }
}
