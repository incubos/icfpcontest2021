package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.List;

import static icfpc2021.ConvexHullKt.area;
import static icfpc2021.ConvexHullKt.convexHull;
import static icfpc2021.ScoringUtils.checkFigure;

public class AutoFoldAction implements Action {

    @Override
    public String toString() {
        return "Autofold action";
    }

    @Override
    public Figure apply(State state, Figure figure) {
        // We try to minimize convex hull area with fold
        List<Vertex> convexHull = convexHull(figure.vertices);
        var convexHullEdges = new HashSet<Pair<Integer, Integer>>();
        for (int i = 0; i < convexHull.size(); i++) {
            convexHullEdges.add(
                    Pair.create(figure.vertices.indexOf(convexHull.get(i)),
                            figure.vertices.indexOf(convexHull.get((i + 1) % convexHull.size())))
            );
        }
        var minArea = area(convexHull);
        FoldAction minFoldAction = null;
        for (int i = 0; i < figure.vertices.size(); i++) {
            for (int j = i + 1; j < figure.vertices.size(); j++) {
                for (int k = 0; k < figure.vertices.size(); k++) {
                    if (k != i && k != j &&
                            !(convexHullEdges.contains(Pair.create(i, j)) ||
                                    convexHullEdges.contains(Pair.create(j, i)))) {
                        FoldAction fa = new FoldAction(i, j, k);
                        var newFigure = fa.apply(state, figure);
                        if (checkFigure(state.getOriginalMan().figure, newFigure, state.getOriginalMan().epsilon)) {
                            var newArea = area(convexHull(newFigure.vertices));
                            if (newArea < minArea) {
                                minArea = newArea;
                                minFoldAction = fa;
                            }
                        }
                    }
                }
            }
        }
        if (minFoldAction != null) {
            return minFoldAction.apply(state, figure);
        }
        return figure;
    }
}
