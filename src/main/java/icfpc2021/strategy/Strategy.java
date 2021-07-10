package icfpc2021.strategy;

import icfpc2021.actions.Action;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.List;

public interface Strategy {
    List<Action> apply(State state, Figure figure);
}
