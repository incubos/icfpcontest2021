package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.*;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

public class AutoCenterStrategy implements Strategy {

    public List<Action> apply(State state, Figure figure) {
        var currentFigure = figure;
        var actions = new ArrayList<Action>();
        final Action autoCenterAction = Action.checked(new AutoCenterAction());
        currentFigure = autoCenterAction.apply(state, currentFigure);
        actions.add(autoCenterAction);

        // Check
        if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
            return actions;
        }

        actions.add(new WiggleAction());
        return actions;
    }
}
