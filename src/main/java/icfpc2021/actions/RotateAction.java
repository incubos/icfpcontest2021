package icfpc2021.actions;

import icfpc2021.model.Figure;

public class RotateAction implements Action {
    public RotateAction(double x, double y, double rotate) {
        this.x = x;
        this.y = y;
        this.rotate = rotate;
    }

    public double x;
    public double y;
    public double rotate;

    @Override
    public Figure apply(Figure figure) {
        return null;
    }
}
