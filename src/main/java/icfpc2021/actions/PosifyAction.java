package icfpc2021.actions;

import icfpc2021.ScoringUtils;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * This action tries to assume position pushing all nodes to nearest integer coordinates one by one.
 */
public class PosifyAction implements Action {
    @Override
    public Figure apply(State state, Figure figure) {
        Figure originalFigure = state.getOriginalMan().figure;
        return doApply(figure, originalFigure);
    }

    @NotNull
    public Figure doApply(Figure figure, Figure originalFigure) {
        List<Vertex> vertices = new ArrayList<>(figure.vertices);
        for (int i = 0; i < figure.vertices.size(); i++) {
            Vertex vertex = figure.vertices.get(i);
            if (!ScoringUtils.isIntegerCoordinates(vertex)) {
                Vertex roundVertex = ScoringUtils.round(i, vertex, vertices, figure.edges, originalFigure);
                vertices.set(i, roundVertex);
            }
        }
        return new Figure(vertices, figure.edges);
    }
}
