package icfpc2021.actions;

import icfpc2021.model.Figure;

public interface Action {
    Figure apply(Figure figure);
}
