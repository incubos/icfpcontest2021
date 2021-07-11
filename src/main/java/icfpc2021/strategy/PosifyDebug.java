package icfpc2021.strategy;

import icfpc2021.ScoringUtils;
import icfpc2021.actions.Action;
import icfpc2021.actions.MoveVertexToGridAction;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;

import java.util.ArrayList;
import java.util.List;

/**
 * PosifyDebug
 */
public class PosifyDebug implements Strategy {

    @Override
    public List<Action> apply(State state, Figure figure) {
        List<Action> actions = new ArrayList<>();

        List<Vertex> vertices = new ArrayList<>(figure.vertices);

        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (!ScoringUtils.isIntegerCoordinates(vertex)) {
                Vertex roundVertex = ScoringUtils.round(i, vertex, vertices, figure.edges, state.getOriginalMan().figure);
                vertices.set(i, roundVertex);
                // IMPORTANT: no checked here!
                actions.add(new MoveVertexToGridAction(i, roundVertex));
            }
        }
        return actions;
    }
}
