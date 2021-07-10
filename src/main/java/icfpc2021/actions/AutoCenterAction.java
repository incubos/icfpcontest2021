package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.List;

import static icfpc2021.ConvexHullKt.convexHull;

public class AutoCenterAction implements Action {

    @Override
    public Figure apply(State state, Figure figure) {
        var figureCenterCoords = figureCenter(convexHull(figure.vertices));
        var holeCenterCoords = figureCenter(state.getHoleConvexHull());
        var dx = holeCenterCoords.x - figureCenterCoords.x;
        var dy = holeCenterCoords.y - figureCenterCoords.y;
        return Action.checked(new MoveAction(dx, dy)).apply(state, figure);
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
