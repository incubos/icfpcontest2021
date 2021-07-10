package icfpc2021.actions;

import icfpc2021.model.Figure;
import icfpc2021.viz.State;

public interface Action {
    Figure apply(State state, Figure figure);
}
