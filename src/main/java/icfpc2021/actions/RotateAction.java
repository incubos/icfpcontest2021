package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.awt.geom.AffineTransform;

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
    public Figure apply(State state, Figure figure) {
        var doubles = figure.verticesToDoubleArray();
        var result = new double[doubles.length];
        var transform = new AffineTransform();
        transform.rotate(Math.toRadians(rotate), x, y);
        transform.transform(doubles, 0, result, 0, doubles.length / 2);
        return figure.copyVerticesFromDoubleArray(result);
    }

    @Override
    public String toString() {
        return "Rotate[" + x + "," + y + "," + rotate + "]";
    }

}
