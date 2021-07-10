package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Hole;

import java.awt.geom.AffineTransform;

public class CenterAction implements Action {
    public CenterAction(Hole hole) {
        this.hole = hole;
    }

    Hole hole;

    @Override
    public Figure apply(Figure figure) {
        var figureCenterCoords = figure.center();
        var holeCenterCoords = hole.center();
        var dx = holeCenterCoords[0] - figureCenterCoords[0];
        var dy = holeCenterCoords[1] - figureCenterCoords[1];
        return new MoveAction(dx, dy).apply(figure);
    }

    @Override
    public String toString() {
        return "Center action";
    }
}
