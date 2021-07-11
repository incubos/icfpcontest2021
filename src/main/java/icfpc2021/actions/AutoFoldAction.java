package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.apache.commons.math3.util.Pair;

import java.util.HashSet;
import java.util.List;

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
        Action minFoldAction = null;
        var notFitting = ScoringUtils.listNotFitting(figure, state.getHole());
        if (notFitting.isEmpty()) {
            return figure;
        }
        var numNotFitting = notFitting.size();
        for (int i : notFitting) {
            for (int k = 0; k < figure.vertices.size(); k++) {
                if (k == i) {
                    continue;
                }
                if (minFoldAction != null) {
                    break;
                }
                for (int j = 0; j < figure.vertices.size(); j++) {
                    if (j == k || j == i) {
                        continue;
                    }
                    if (minFoldAction != null) {
                        break;
                    }
                    if (!(convexHullEdges.contains(Pair.create(k, j)) || convexHullEdges.contains(Pair.create(j, k)))) {
                        Action fa = Action.checked(new FoldAction(j, k, i));
                        var newFigure = fa.apply(state, figure);
                        if (!figure.equals(newFigure) && checkFigure(newFigure, state.getOriginalMan().figure, state.getOriginalMan().epsilon)) {
                            var newNotFitting = ScoringUtils.listNotFitting(newFigure, state.getHole());
                            if (newNotFitting.size() < numNotFitting) {
                                numNotFitting = newNotFitting.size();
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
