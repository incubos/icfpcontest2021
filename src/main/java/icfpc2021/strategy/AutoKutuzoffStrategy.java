package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.*;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

public class AutoKutuzoffStrategy implements Strategy {
    private static final int CYCLES = 3;
    private static final int FOLDS = 10;
    private static final int ROTATES = 5;

    @Override
    public List<Action> apply(State state, Figure figure) {
        var currentFigure = figure;
        var actions = new ArrayList<Action>();
        for (int count = 0; count < CYCLES; count++) {
            var cycleStartFigure = currentFigure;
            // Check
            if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
                return actions;
            }
            // Folding
            for (int i = 0; i < FOLDS; i++) {
                Action a = Action.checked(new AutoFoldAction());
                var newFigure = a.apply(state, currentFigure);
                if (!newFigure.equals(currentFigure)) {
                    currentFigure = newFigure;
                    actions.add(a);
                    // Check
                    if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
                        return actions;
                    }
                } else {
                    break;
                }
            }
            for (int i = 0; i < ROTATES; i++) {
                Action a = Action.checked(new AutoRotateAction());
                var newFigure = a.apply(state, currentFigure);
                if (!newFigure.equals(currentFigure)) {
                    currentFigure = newFigure;
                    actions.add(a);
                    // Check
                    if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
                        return actions;
                    }
                } else {
                    break;
                }
            }
            final Action autoCenterAction = Action.checked(new AutoCenterAction());
            currentFigure = autoCenterAction.apply(state, currentFigure);
            actions.add(autoCenterAction);

            // Check
            if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
                return actions;
            }

            // Slightly move to fit if we can
            Action wa = Action.checked(new WiggleAction());
            var wFigure = wa.apply(state, currentFigure);
            if (!wFigure.equals(currentFigure)) {
                currentFigure = wFigure;
                actions.add(wa);
            }


            Action a = Action.checked(new AutoFoldAreaAction());
            var fFigure = a.apply(state, currentFigure);
            if (!fFigure.equals(currentFigure)) {
                currentFigure = fFigure;
                actions.add(a);
            }
            // Give up if actions are useless
            if (cycleStartFigure.equals(currentFigure)) {
                break;
            }
        }
        return actions;
    }
}
