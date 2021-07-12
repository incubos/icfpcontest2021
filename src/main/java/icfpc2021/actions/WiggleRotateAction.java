package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import static icfpc2021.actions.AutoCenterAction.figureCenter;
import static icfpc2021.actions.AutoRotateAction.ANGLE;

public class WiggleRotateAction implements Action {


    @Override
    public Figure apply(State state, Figure figure) {
        // Check
        if (ScoringUtils.fitsWithinHole(figure, state.getHole())) {
            return figure;
        }
        var minNotFitting = ScoringUtils.listNotFitting(figure, state.getHole()).size();
        Figure minFigure = figure;
        int x = ANGLE;
        Vertex centerVertex = figureCenter(figure.vertices);
        while (x < 360) {
            Action rotateAction = Action.checked(new RotateAction(centerVertex.x, centerVertex.y, ANGLE));
            Figure slightlyRotated = rotateAction.apply(state, figure);
            if (ScoringUtils.fitsWithinHole(slightlyRotated, state.getHole())) {
                return slightlyRotated;
            } else {
                var notFitting = ScoringUtils.listNotFitting(slightlyRotated, state.getHole()).size();
                if (notFitting < minNotFitting) {
                    minNotFitting = notFitting;
                    minFigure = slightlyRotated;
                }
            }
            x += ANGLE;
        }
        return minFigure;
    }

    @Override
    public String toString() {
       return "WiggleRotate";
    }
}
