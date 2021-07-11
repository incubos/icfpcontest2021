package icfpc2021.strategy;

import icfpc2021.actions.Action;
import icfpc2021.actions.SMTSolverAction;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.List;

public class SolverStrategy implements Strategy {
    @Override
    public List<Action> apply(final State state, final Figure figure) {
        return List.of(Action.checked(new SMTSolverAction()));
    }
}
