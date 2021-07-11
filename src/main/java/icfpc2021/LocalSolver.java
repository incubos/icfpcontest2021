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
import icfpc2021.strategy.AutoCenterStrategy;
import icfpc2021.strategy.AutoKutuzoffStrategy;
import icfpc2021.strategy.PosifyEdges;
import icfpc2021.strategy.Strategy;
import icfpc2021.viz.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class LocalSolver {

    public static final List<Strategy> STRATEGIES = List.of(new AutoCenterStrategy(), new AutoKutuzoffStrategy());
    private static ObjectMapper objectMapper = new ObjectMapper();

    // Ignore for now
    private static Set<Integer> IGNORED = Set.of(7);
    public static final int LARGE_FIGURE = 10000;

    public static void main(String[] args) throws IOException {
        int correctValues = 0;
        int fitted = 0;
        int solved = 0;
        for (int i = 1; i <= 106; i++) {

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

            for (Strategy strategy : STRATEGIES) {
                figure = state.getOriginalMan().figure;
                for (Action action : strategy.apply(state, figure)) {
                    figure = action.apply(state, figure);
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
                boolean fits = ScoringUtils.fitsWithinHole(figure, state.getHole());
                if (correct && fits) {
                    break;
                }
            }

            boolean correct = ScoringUtils.checkFigure(figure, lambdaMan.figure, lambdaMan.epsilon);
            boolean fits = ScoringUtils.fitsWithinHole(figure, state.getHole());
            if (correct) {
                correctValues += 1;
            }
            if (fits) {
                fitted += 1;
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

            if (correct && fits) {
                solved += 1;
                Pose pose = Pose.fromVertices(figure.vertices);
                String json = objectMapper.writeValueAsString(pose);
                Files.writeString(solutionPath, json);
            }
            System.out.println("Correct " + correct + "; Fits " + fits);
        }
        System.out.println("Total correct " + correctValues + "; Total fits " + fitted + "; Total solved " + solved);
    }
}
