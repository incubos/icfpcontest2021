package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.Action;
import icfpc2021.actions.SMTSolverAction;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.List;

/**
 * TODO
 *
 * @author incubos
 */
public class SolverStrategy implements Strategy {
    @Override
    public List<Action> apply(final State state, final Figure figure) {
        final Action solverAction = Action.checked(new SMTSolverAction());
        final Figure currentFigure = solverAction.apply(state, figure);

        // Check
        if (ScoringUtils.fitsWithinHole(currentFigure, state.getHole())) {
            return List.of(solverAction);
        }

        return List.of();
    }
}
