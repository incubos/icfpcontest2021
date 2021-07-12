package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.Action;
import icfpc2021.actions.MoveVertexToGridAction;
import icfpc2021.geom.GridDirection;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.LambdaMan;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static icfpc2021.ScoringUtils.STAY_IN_GREED;
import static icfpc2021.ScoringUtils.getEdgeCorrectRounds;

/**
 * Posify by edges
 */
public class PosifyEdges implements Strategy {

    private static final int THRESHOLD = 30;

    private static List<Pair<GridDirection, GridDirection>> STAY_VARIANTS = List.of(STAY_IN_GREED);

    @Override
    public List<Action> apply(State state, Figure figure) {
        HashMap<Integer, List<Integer>> adjacencyList = state.getAdjacencyList();
        LambdaMan originalMan = state.getOriginalMan();
        return new ArrayList<>(doApply(figure, adjacencyList, originalMan));
    }

    @NotNull
    public List<MoveVertexToGridAction> doApply(Figure figure, HashMap<Integer, List<Integer>> adjacencyList, LambdaMan originalMan) {
        Map<Integer, List<Pair<GridDirection, GridDirection>>> nonGridEdgeFixes = collectFixes(figure, originalMan);
        if (nonGridEdgeFixes == null) {
            System.out.println("Non fixable edge found");
            return Collections.emptyList();
        }
        if (nonGridEdgeFixes.isEmpty()) {
            // Everything is OK
            return Collections.emptyList();
        }

        if (nonGridEdgeFixes.size() > THRESHOLD) {
            // Too many fixes
            System.out.println("Too big size for spotify edges");
            return Collections.emptyList();
        }

        // Exponential search, start with top connected edges
        List<Integer> edgesOrder = nonGridEdgeFixes.keySet().stream().sorted((e1, e2) -> {
            if (e1.equals(e2)) {
                return 0;
            }
            Edge edge1 = figure.edges.get(e1);
            Edge edge2 = figure.edges.get(e2);
            return -Integer.compare(Math.max(adjacencyList.get(edge1.start).size(),
                    adjacencyList.get(edge1.end).size()),
                    Math.max(adjacencyList.get(edge2.start).size(),
                            adjacencyList.get(edge2.end).size()));
        }).collect(Collectors.toList());

        Map<Integer, GridDirection> verticesFixes =
                searchVerticesFixes(edgesOrder, 0, nonGridEdgeFixes, Collections.emptyMap(), figure.edges);
        // Nothing found
        if (verticesFixes == null) {
            System.out.println("Spotify edges failed to find combination");
            return Collections.emptyList();
        }
        return verticesFixes.entrySet().stream().map(entry -> {
            Integer v = entry.getKey();
            return new MoveVertexToGridAction(v, entry.getValue().move(figure.vertices.get(v)));
        }).collect(Collectors.toList());
    }


    /**
     * Returns null if some non-grid edges cannot be posified
     */
    public static Map<Integer, List<Pair<GridDirection, GridDirection>>> collectFixes(Figure figure, LambdaMan originalMan) {
        // Save all valid variants
        HashMap<Integer, List<Pair<GridDirection, GridDirection>>> nonGridEdgeFixes = new HashMap<>();

        final double[] originalEdgesLength = ScoringUtils.edgeSquareLengthsFrom(
                originalMan.figure.vertices, originalMan.figure.edges);
        final double threshold = originalMan.epsilon / 1_000_000.0;

        // Collect edges move variants
        for (int i = 0; i < figure.edges.size(); i++) {
            Edge edge = figure.edges.get(i);
            Vertex vStart = figure.vertices.get(edge.start);
            Vertex vEnd = figure.vertices.get(edge.end);
            List<Pair<GridDirection, GridDirection>> fixes =
                    getEdgeCorrectRounds(vStart, vEnd, originalEdgesLength[i], threshold);
            if (!fixes.equals(STAY_VARIANTS)) {
                nonGridEdgeFixes.put(i, fixes);
            }
            if (fixes.isEmpty()) {
                // Cannot fix it, burn!
                return null;
            }
        }
        return nonGridEdgeFixes;
    }

    /**
     * Returns map vertex -> fix, or null if nothing found
     */
    Map<Integer, GridDirection>
    searchVerticesFixes(List<Integer> edgesOrder,
                        int i,
                        Map<Integer, List<Pair<GridDirection, GridDirection>>> fixes,
                        Map<Integer, GridDirection> verticesAssigned,
                        List<Edge> edges) {
        if (i == edges.size()) {
            return verticesAssigned;
        }
        Edge edge = edges.get(edgesOrder.get(i));
        List<Pair<GridDirection, GridDirection>> edgeFixes = fixes.get(edgesOrder.get(i));
        for (Pair<GridDirection, GridDirection> edgeFix : edgeFixes) {
            GridDirection start = edgeFix.getFirst();
            GridDirection end = edgeFix.getSecond();
            GridDirection startAssigned = verticesAssigned.get(edge.start);
            GridDirection endAssigned = verticesAssigned.get(edge.end);
            if (startAssigned != null && startAssigned != start || endAssigned != null && endAssigned != end) {
                continue;
            }
            if (startAssigned == start && endAssigned == end) {
                Map<Integer, GridDirection> search = searchVerticesFixes(edgesOrder, i + 1, fixes, verticesAssigned, edges);
                if (search != null) {
                    return search;
                }
            }
            HashMap<Integer, GridDirection> newAssignment = new HashMap<>(verticesAssigned);
            newAssignment.put(edge.start, start);
            newAssignment.put(edge.end, end);
            Map<Integer, GridDirection> search = searchVerticesFixes(edgesOrder, i + 1, fixes, newAssignment, edges);
            if (search != null) {
                return search;
            }
        }
        return null;
    }
}
