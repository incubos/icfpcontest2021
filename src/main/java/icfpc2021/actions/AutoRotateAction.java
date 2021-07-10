package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;

import java.awt.geom.AffineTransform;
import java.util.List;

import static icfpc2021.ConvexHullKt.convexHull;
import static icfpc2021.geom.PCAKt.principalComponents;

public class AutoRotateAction implements Action {
    public AutoRotateAction(List<Vertex> holeConvexHull) {
        this.holeConvexHull = holeConvexHull;
    }

    List<Vertex> holeConvexHull;

    @Override
    public Figure apply(Figure figure) {
        var pcaHole = principalComponents(holeConvexHull);
        var figureConvexHull = convexHull(figure.vertices);
        var pcaFigure = principalComponents(figureConvexHull);
        var mainDirectionHole = pcaHole.get(0);
        var mainDirectionFigure = pcaFigure.get(0);
        double cos = mainDirectionFigure.x * mainDirectionHole.x +  mainDirectionFigure.y * mainDirectionHole.y /
                (length(mainDirectionHole) * length(mainDirectionFigure));
        var rads = Math.acos(cos);
        var degrees = Math.toDegrees(rads);
        double[] center = figure.center();
        return new RotateAction(center[0], center[1], -degrees).apply(figure);
    }

    private double length(Vertex vector) {
        return Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }
    @Override
    public String toString() {
        return "Autorotate";
    }

}
