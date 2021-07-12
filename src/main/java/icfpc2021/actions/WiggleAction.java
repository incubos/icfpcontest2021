package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

public class WiggleAction implements Action {

    // Percentage of grid to move
    private static final double MOVE_DELTA = 0.1;

    @Override
    public Figure apply(State state, Figure figure) {
        // Check
        if (ScoringUtils.fitsWithinHole(figure, state.getHole())) {
            return figure;
        }
        var minNotFitting = ScoringUtils.listNotFitting(figure, state.getHole()).size();
        Figure minFigure = figure;
        int moveDelta = (int) Math.round(state.maxCs() - state.minCs() * MOVE_DELTA);
        for (int x = -moveDelta; x <= moveDelta; x++) {
            for (int y = -moveDelta; y <= moveDelta; y++) {
                Action moveAction = Action.checked(new MoveAction(x, y));
                Figure slightlyMoved = moveAction.apply(state, figure);
                if (ScoringUtils.fitsWithinHole(slightlyMoved, state.getHole())) {
                    return slightlyMoved;
                } else {
                    var notFitting = ScoringUtils.listNotFitting(slightlyMoved, state.getHole()).size();
                    if (notFitting < minNotFitting) {
                        minNotFitting = notFitting;
                        minFigure = slightlyMoved;
                    }
                }
            }
        }
        return minFigure;
    }

    @Override
    public String toString() {
       return "Wiggle";
    }
}
