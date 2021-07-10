package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

/**
 * This action tries to assume position pushing all nodes to nearest integer coordiates.
 */
public class PosifyAction implements Action {

    enum Direction {
        UP_RIGHT(1, -1),
        DOWN_RIGHT(1, 0),
        UP_LEFT(0, -1),
        DOWN_LEFT(0, 0);

        final int dx, dy;

        Direction(final int dx, final int dy) {
            this.dx = dx;
            this.dy = dy;
        }
    }

    public PosifyAction() {
    }

    private boolean isIntCoordiates(Vertex vertex) {
        return Math.floor(vertex.x) == vertex.x && Math.floor(vertex.y) == vertex.y;
    }

    @Override
    public Figure apply(State state, Figure figure) {
        Figure resultFigure = figure;
        List<Vertex> vertices = new ArrayList<>(figure.vertices);
        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (!isIntCoordiates(vertex)) {
                for (Direction d : Direction.values()) {
                    vertices.set(i, new Vertex(Math.floor(vertex.x) + d.dx, Math.floor(vertex.y) + d.dy));
                    Figure tmpFigure = new Figure(vertices, figure.edges);
                    if (ScoringUtils.fitsWithinHole(tmpFigure,state.getHole())) {
                        resultFigure = tmpFigure;
                        break;
                    }
                }
            }
        }
        return resultFigure;
    }
}
