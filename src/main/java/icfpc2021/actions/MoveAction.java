package icfpc2021.actions;

import icfpc2021.model.Figure;

import java.awt.geom.AffineTransform;

public class MoveAction implements Action {
    public MoveAction(double dX, double dY) {
        this.dX = dX;
        this.dY = dY;
    }

    public double dX;
    public double dY;

    @Override
    public Figure apply(Figure figure) {
        var doubles = figure.verticesToDoubleArray();
        var result = new double[doubles.length];
        var transform = new AffineTransform();
        transform.translate(dX, dY);
        transform.transform(doubles, 0, result, 0, doubles.length / 2);
        return figure.copyVerticesFromDoubleArray(result);
    }

    @Override
    public String toString() {
        return "Move[" + dX + "," + dY + "]";
    }
}
