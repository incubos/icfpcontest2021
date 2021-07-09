package icfpc2021.actions;

import icfpc2021.model.Figure;

public class MoveAction implements Action {
    public MoveAction(double dX, double dY) {
        this.dX = dX;
        this.dY = dY;
    }

    public double dX;
    public double dY;

    @Override
    public Figure apply(Figure figure) {
        return null;
    }
}
