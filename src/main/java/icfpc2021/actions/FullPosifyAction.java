package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Hole;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This action tries to assume position moving all the figure to
 * push most of the vertices to nearest integer coordinates.
 */
public class FullPosifyAction implements Action {
    private static final Logger log = LoggerFactory.getLogger(FullPosifyAction.class);

    @Override
    public Figure apply(State state, Figure figure) {
        Hole hole = state.getHole();
        return doApply(figure, hole);
    }

    @NotNull
    public Figure doApply(Figure figure, Hole hole) {
        // Analyze avg fraction over all the vertices
        double dx = 0.0;
        double dy = 0.0;
        for (final Vertex vertex : figure.vertices) {
            dx += vertex.x - Math.floor(vertex.x);
            dy += vertex.y - Math.floor(vertex.y);
        }
        dx /= figure.vertices.size();
        dy /= figure.vertices.size();

        // Try global movements
        final Iterable<Move> candidates =
                List.of(
                        new Move(-dx, -dy),
                        new Move(-dx, 0.0),
                        new Move(0.0, -dy),
                        new Move(1.0 - dx, 1.0 - dy),
                        new Move(1.0 - dx, 0.0),
                        new Move(0.0, 1.0 - dy),
                        new Move(-dx, 1.0 - dy),
                        new Move(1.0 - dx, -dy));

        // Choose movement that fits the hole and minimizes cumulative fraction
        double bestCumulativeFraction = Double.MAX_VALUE;
        Move bestMove = null;
        Figure bestResult = null;
        for (final Move move : candidates) {
            // Move vertices
            List<Vertex> vertices = new ArrayList<>(figure.vertices);
            for (int i = 0; i < vertices.size(); i++) {
                vertices.set(i, vertices.get(i).move(move.dx, move.dy));
            }

            // Check that fits the hole
            final Figure result = figure.withVertices(vertices);
            if (!ScoringUtils.fitsWithinHole(result, hole)) {
                continue;
            }

            // Score
            double cumulativeFraction = 0.0;
            for (final Vertex vertex : vertices) {
                cumulativeFraction += Math.abs(vertex.x - Math.round(vertex.x));
                cumulativeFraction += Math.abs(vertex.y - Math.round(vertex.y));
            }

            // Compare
            if (bestCumulativeFraction > cumulativeFraction) {
                bestCumulativeFraction = cumulativeFraction;
                bestMove = move;
                bestResult = result;
            }
        }

        if (bestResult == null) {
            log.warn("Can't shift whole figure and stay in the hole");
            return figure;
        }

        log.info("Applied {} to whole figure with cumulative fractional {}", bestMove, bestCumulativeFraction);

        return bestResult;
    }

    private static class Move {
        private final double dx;
        private final double dy;

        private Move(final double dx, final double dy) {
            this.dx = dx;
            this.dy = dy;
        }

        @Override
        public String toString() {
            return "Move{" +
                    "dx=" + dx +
                    ", dy=" + dy +
                    '}';
        }
    }
}
