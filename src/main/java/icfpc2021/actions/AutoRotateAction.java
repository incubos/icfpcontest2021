package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import static icfpc2021.ConvexHullKt.convexHull;
import static icfpc2021.actions.AutoCenterAction.figureCenter;
import static icfpc2021.geom.PCAKt.principalComponents;

public class AutoRotateAction implements Action {
    @Override
    public Figure apply(State state, Figure figure) {
        try {
            var pcaHole = principalComponents(state.getHoleConvexHull());
            var figureConvexHull = convexHull(figure.vertices);
            var pcaFigure = principalComponents(figureConvexHull);
            var mainDirectionHole = pcaHole.get(0);
            var mainDirectionFigure = pcaFigure.get(0);
            double cos = mainDirectionFigure.x * mainDirectionHole.x +  mainDirectionFigure.y * mainDirectionHole.y /
                    (length(mainDirectionHole) * length(mainDirectionFigure));
            var rads = Math.acos(cos);
            var degrees = Math.toDegrees(rads);
            Vertex centerVertex = figureCenter(figure.vertices);
            return new RotateAction(centerVertex.x, centerVertex.y, -degrees).apply(state, figure);
        } catch (Exception e) {
            // Ignore
            return figure;
        }
    }

    private double length(Vertex vector) {
        return Math.hypot(vector.x, vector.y);
    }
    @Override
    public String toString() {
        return "Autorotate";
    }

}
