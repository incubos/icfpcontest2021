package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.Action;
import icfpc2021.actions.MoveEdgeToGridAction;
import icfpc2021.actions.MoveVertexToGridAction;
import icfpc2021.geom.GridDirection;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.apache.commons.math3.util.Pair;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static icfpc2021.ScoringUtils.*;

/**
 * PosifyDebug
 */
public class PosifyDebug implements Strategy {

    private static List<Pair<GridDirection, GridDirection>> STAY_VARIANTS = List.of(STAY_IN_GREED);

    @Override
    public List<Action> apply(State state, Figure figure) {
        ArrayList<Integer> nonGridEdges = new ArrayList<>();
        // Save all valid variants
        ArrayList<List<Pair<GridDirection, GridDirection>>> nonGridEdgeFixes = new ArrayList<>();

        final double[] originalEdgesLength = ScoringUtils.edgeSquareLengthsFrom(
                state.getOriginalMan().figure.vertices, state.getOriginalMan().figure.edges);
        final double threshold = state.getOriginalMan().epsilon / 1_000_000.0;

        // Collect edges move variants
        for (int i = 0; i < figure.edges.size(); i++) {
            Edge edge = figure.edges.get(i);
            Vertex vStart = figure.vertices.get(edge.start);
            Vertex vEnd = figure.vertices.get(edge.end);
            List<Pair<GridDirection, GridDirection>> fixes =
                    getEdgeCorrectRounds(vStart, vEnd, originalEdgesLength[i], threshold);
            if (!fixes.equals(STAY_VARIANTS)) {
                nonGridEdges.add(i);
                nonGridEdgeFixes.add(fixes);
            }
            if (fixes.isEmpty()) {
                // Cannot fix it, burn!
                return Collections.emptyList();
            }
        }

        if (nonGridEdges.size() == 0) {
            return Collections.emptyList();
        }


        // TODO: All variants are collected, start search.
        ArrayList<Action> actions = new ArrayList<>();
        // Collect fixes frequencies
        HashMap<Pair<GridDirection, GridDirection>, Integer> fixesCounters = new HashMap<>();
        for (List<Pair<GridDirection, GridDirection>> fixes : nonGridEdgeFixes) {
            for (Pair<GridDirection, GridDirection> fix : fixes) {
                fixesCounters.put(fix, fixesCounters.getOrDefault(fix, 0) + 1);
            }
        }
        // Check for bingo - simple move to ints!
        for (Map.Entry<Pair<GridDirection, GridDirection>, Integer> pair : fixesCounters.entrySet()) {
            var fix = pair.getKey();
            var count = pair.getValue();
            // Bingo, move everything to a single direction!
            if (fix.getFirst().equals(fix.getSecond()) && count == nonGridEdges.size()) {
                return nonGridEdges.stream().map(e -> {
                    Edge edge = figure.edges.get(e);
                    return new MoveEdgeToGridAction(fix, edge.start, edge.end);
                }).collect(Collectors.toList());
            }
        }
        return actions;
    }
}
