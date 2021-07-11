package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.viz.State;

import java.util.Arrays;

/**
 * Checks that delegate {@link Action} doesn't break invariants.
 */
public class CheckingAction implements Action {
    private final Action delegate;

    public CheckingAction(final Action delegate) {
        this.delegate = delegate;
    }

    @Override
    public Figure apply(final State state, final Figure figure) {
        assert ScoringUtils.checkFigure(figure, state.getOriginalMan().figure, state.getOriginalMan().epsilon);
        final Figure result = delegate.apply(state, figure);
        assert ScoringUtils.checkFigure(result, state.getOriginalMan().figure, state.getOriginalMan().epsilon);
        //if (!ScoringUtils.checkFigure(result, state.getOriginalMan().figure, state.getOriginalMan().epsilon)) {
/*
        if (delegate instanceof PosifyAction) {
            System.out.println("Prevous: " + figure);
            System.out.println("Current: " + result);
            final double[] previousSquareLengths = ScoringUtils.edgeSquareLengthsFrom(figure.vertices, figure.edges);
            System.out.println("Previous lengths: " + Arrays.toString(previousSquareLengths));
            final double[] ourSquareLengths = ScoringUtils.edgeSquareLengthsFrom(result.vertices, result.edges);
            System.out.println("Our lengths: " + Arrays.toString(ourSquareLengths));
            final int[] epsilons = new int[figure.edges.size()];
            for (int a = 0; a < epsilons.length; a++) {
                epsilons[a] = (int) Math.ceil(Math.abs(ourSquareLengths[a] / previousSquareLengths[a] - 1.0) * 1_000_000);
            }
            System.out.println("Epsilons: " + Arrays.toString(epsilons));
        }
*/
        return result;
    }

    @Override
    public String toString() {
        return delegate.toString();
    }
}
