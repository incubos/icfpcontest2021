package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

public class WiggleAction implements Action {

    private static final int MOVE_DELTA = 10;

    @Override
    public Figure apply(State state, Figure figure) {
        // Check
        if (ScoringUtils.fitsWithinHole(figure, state.getHole())) {
            return figure;
        }

        for (int x = -MOVE_DELTA; x <= MOVE_DELTA; x++) {
            for (int y = -MOVE_DELTA; y <= MOVE_DELTA; y++) {
                Action moveAction = Action.checked(new MoveAction(x, y));
                Figure slightlyMoved = moveAction.apply(state, figure);
                if (ScoringUtils.fitsWithinHole(slightlyMoved, state.getHole())) {
                    return slightlyMoved;
                }
            }
        }
        return figure;
    }

    @Override
    public String toString() {
       return "Wiggle";
    }
}
