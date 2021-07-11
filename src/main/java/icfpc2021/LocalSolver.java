package icfpc2021;

import com.fasterxml.jackson.databind.ObjectMapper;
import icfpc2021.actions.Action;
import icfpc2021.actions.FullPosifyAction;
import icfpc2021.actions.PosifyAction;
import icfpc2021.actions.WiggleAction;
import icfpc2021.model.Figure;
import icfpc2021.model.LambdaMan;
import icfpc2021.model.Pose;
import icfpc2021.model.Task;
import icfpc2021.strategy.*;
import icfpc2021.viz.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LocalSolver {

    public static final List<Strategy> STRATEGIES = List.of(new AutoCenterStrategy(), new AutoKutuzoffStrategy(), new SolverStrategy());
    private static final Logger log = LoggerFactory.getLogger(LocalSolver.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    // Ignore for now
    private static Set<Integer> IGNORED = Set.of(7);
    public static final int LARGE_FIGURE = 10000;

    public static void main(String[] args) throws IOException {
        int correctValues = 0;
        int inHoles = 0;
        int inGrids = 0;
        int solved = 0;
        for (int i = 1; i <= 132; i++) {
            System.out.println("Problem " + i);
            if (IGNORED.contains(i)) {
                System.out.println("IGNORED");
                continue;
            }
            String taskName = String.format("%03d.json", i);
            Path solutionPath = Path.of("solutions", taskName);
            if (Files.exists(solutionPath)) {
                System.out.println("ALREADY Solved");
                continue;
            }
            Path problemPath = Path.of("problems", taskName);
            var task = Task.fromJsonFile(problemPath);
            LambdaMan lambdaMan = new LambdaMan();
            lambdaMan.epsilon = task.epsilon;
            lambdaMan.figure = task.figure;
            State state = new State(task.hole, lambdaMan, taskName, problemPath);
            System.out.println("Hole size " + task.hole.vertices.size() +
                    "; Man vertices " + lambdaMan.figure.vertices.size() +
                    "; Man edges " + lambdaMan.figure.edges.size() +
                    "; Epsilon " + lambdaMan.epsilon);
            if (lambdaMan.figure.vertices.size() * lambdaMan.figure.edges.size() > LARGE_FIGURE) {
                System.out.println("TOO LARGE");
                continue;
            }

            Figure figure = state.getOriginalMan().figure;

            skip: for (Strategy strategy : STRATEGIES) {
                figure = state.getOriginalMan().figure;
                for (Action action : strategy.apply(state, figure)) {
                    final Figure next = action.apply(state, figure);
                    if (next == figure) {
                        log.warn("Skipping strategy {}", strategy);
                        continue skip;
                    }
                    figure = next;
                }
                figure = Action.checked(new WiggleAction()).apply(state, figure);

                figure = Action.checked(new FullPosifyAction()).apply(state, figure);
                figure = Action.checked(new WiggleAction()).apply(state, figure);

                figure = Action.checked(new PosifyAction()).apply(state, figure);
                figure = Action.checked(new WiggleAction()).apply(state, figure);

                for (Action action : new PosifyEdges().apply(state, figure)) {
                    figure = action.apply(state, figure);
                }
                figure = Action.checked(new WiggleAction()).apply(state, figure);

                boolean correct = ScoringUtils.checkFigure(figure, lambdaMan.figure, lambdaMan.epsilon);
                boolean inHole = ScoringUtils.fitsWithinHole(figure, state.getHole());
                boolean inGrid = ScoringUtils.isFigureInGrid(figure);
                if (correct && inHole && inGrid) {
                    break;
                }
            }

            boolean correct = ScoringUtils.checkFigure(figure, lambdaMan.figure, lambdaMan.epsilon);
            boolean inHole = ScoringUtils.fitsWithinHole(figure, state.getHole());
            boolean inGrid = ScoringUtils.isFigureInGrid(figure);
            if (correct) {
                correctValues += 1;
            }
            if (inHole) {
                inHoles += 1;
            }
            if (inGrid) {
                inGrids += 1;
            }
            System.out.println("Solution " + i);
            final double[] originalSquareLengths = ScoringUtils.edgeSquareLengthsFrom(lambdaMan.figure.vertices, lambdaMan.figure.edges);
            System.out.println("Original lengths: " + Arrays.toString(originalSquareLengths));
            final double[] ourSquareLengths = ScoringUtils.edgeSquareLengthsFrom(figure.vertices, figure.edges);
            System.out.println("Our lengths: " + Arrays.toString(ourSquareLengths));
            final int[] epsilons = new int[figure.edges.size()];
            for (int a = 0; a < epsilons.length; a++) {
                epsilons[a] = (int) Math.ceil(Math.abs(ourSquareLengths[a] / originalSquareLengths[a] - 1.0) * 1_000_000);
            }
            System.out.println("Epsilons: " + Arrays.toString(epsilons));

            if (correct && inHole && inGrid) {
                solved += 1;
                Pose pose = Pose.fromVertices(figure.vertices);
                String json = objectMapper.writeValueAsString(pose);
                Files.writeString(solutionPath, json);
            }
            System.out.println("Correct " + correct + "; Fits " + inHole + "; In grid " + inGrid);
        }
        System.out.println("Total solved " + solved +
                "; Total correct " + correctValues + "; Total fits " + inHoles + "; Total in grid " + inGrids);
    }
}
