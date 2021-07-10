package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.List;

import static icfpc2021.ConvexHullKt.convexHull;

public class AutoCenterAction implements Action {

    int slightlyMoveDelta = 3;

    @Override
    public Figure apply(State state, Figure figure) {
        var figureCenterCoords = figureCenter(convexHull(figure.vertices));
        var holeCenterCoords = figureCenter(state.getHoleConvexHull());
        var dx = holeCenterCoords.x - figureCenterCoords.x;
        var dy = holeCenterCoords.y - figureCenterCoords.y;
        MoveAction moveAction = new MoveAction(dx, dy);
        Figure movedFigure = moveAction.apply(state, figure);
        if (ScoringUtils.fitsWithinHole(figure, state.getHole())) {
            return movedFigure;
        }
        // Slightly move to fit if we can
        for (int x = -slightlyMoveDelta; x <= slightlyMoveDelta; x++) {
            for (int y = -slightlyMoveDelta; y <= slightlyMoveDelta; y++) {
                Figure slightlyMoved = new MoveAction(x, y).apply(state, movedFigure);
                if (ScoringUtils.fitsWithinHole(slightlyMoved, state.getHole())) {
                    return slightlyMoved;
                }
            }
        }
        return movedFigure;
    }

    public static Vertex figureCenter(List<Vertex> vertices) {
        return new Vertex(
                vertices.stream().mapToDouble(v -> v.x).average().getAsDouble(),
                vertices.stream().mapToDouble(v -> v.y).average().getAsDouble()
        );
    }

    @Override
    public String toString() {
        return "AutoCenter action";
    }
}
