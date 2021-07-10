package icfpc2021.strategy;

import icfpc2021.actions.Action;
import icfpc2021.actions.AutoCenterAction;
import icfpc2021.actions.AutoFoldAction;
import icfpc2021.actions.AutoRotateAction;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

public class AutoKutuzoffStrategy implements Strategy {
    @Override
    public List<Action> apply(State state, Figure figure) {
        var result = new ArrayList<Action>();
        for (int i = 0; i < 10; i++) {
            //TODO add modification check
            result.add(new AutoFoldAction());
        }
        for (int i = 0; i < 5; i++) {
            // TODO add modification check
            result.add(new AutoRotateAction());
        }
        result.add(new AutoCenterAction());
        return result;
    }
}
