package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;

import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

public class MoveAction implements Action {
    public MoveAction(double dX, double dY) {
        this.dX = dX;
        this.dY = dY;
    }

    public double dX;
    public double dY;

    @Override
    public Figure apply(Figure figure) {
        var doubles = figureVerticesToDoubleArray(figure);
        var result = new double[doubles.length];
        var transform = new AffineTransform();
        transform.translate(dX, dY);
        transform.transform(doubles, 0, result, 0, doubles.length / 2);
        var vertices = doubleArrayToVertices(result);
        return new Figure(vertices, figure.edges);
    }

    public static double[] figureVerticesToDoubleArray(Figure figure) {
        return figure.vertices
                .stream()
                .flatMapToDouble(v -> DoubleStream.of(v.x, v.y))
                .toArray();
    }

    public static List<Vertex> doubleArrayToVertices(double[] array) {
        var result = new ArrayList<Vertex>();
        for (int i = 0; i < array.length; i += 2) {
            result.add(new Vertex(array[i], array[i + 1]));
        }
        return result;
    }
}
