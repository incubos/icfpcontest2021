package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

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
        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (!isIntCoordiates(vertex)) {
                for (Direction d : Direction.values()) {
                    Figure tmpFigure = new PushVertexAction(i, (int) Math.floor(vertex.x) + d.dx, (int) Math.floor(vertex.y) + d.dy).apply(state, figure);
                    if (ScoringUtils.fitsWithinHole(tmpFigure,state.getHole())) {
                        resultFigure = tmpFigure;
                    }
                }
            }
        }
        return resultFigure;
    }
}
