package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.*;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

public class AutoKutuzoffStrategy implements Strategy {
    private int slightlyMoveDelta = 5;

    @Override
    public List<Action> apply(State state, Figure figure) {
        var currentFigure = figure;
        var actions = new ArrayList<Action>();

        // Check
        if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
            return actions;
        }
        // Folde
        for (int i = 0; i < 10; i++) {
            Action a = new AutoFoldAction();
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
        for (int i = 0; i < 5; i++) {
            Action a = new AutoRotateAction();
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
        final Action autoCenterAction = new AutoCenterAction();
        currentFigure = autoCenterAction.apply(state, currentFigure);
        actions.add(autoCenterAction);

        // Check
        if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
            return actions;
        }

        // Slightly move to fit if we can
        for (int x = -slightlyMoveDelta; x <= slightlyMoveDelta; x++) {
            for (int y = -slightlyMoveDelta; y <= slightlyMoveDelta; y++) {
                MoveAction moveAction = new MoveAction(x, y);
                Figure slightlyMoved = moveAction.apply(state, currentFigure);
                if (ScoringUtils.fitsWithinHole(slightlyMoved, state.getHole())) {
                    actions.add(moveAction);
                    return actions;
                }
            }
        }
        return actions;
    }
}
