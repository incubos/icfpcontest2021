package icfpc2021;

import com.fasterxml.jackson.databind.ObjectMapper;
import icfpc2021.model.Figure;
import icfpc2021.model.LambdaMan;
import icfpc2021.model.Pose;
import icfpc2021.model.Task;
import icfpc2021.strategy.AutoKutuzoffStrategy;
import icfpc2021.viz.State;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

public class LocalSolver {

    private static ObjectMapper objectMapper = new ObjectMapper();

    // Ignore for now
    private static Set<Integer> IGNORED = Set.of(7);

    public static void main(String[] args) throws IOException {
        int correctValues = 0;
        int fitted = 0;
        int solved = 0;
        for (int i = 1; i < 78; i++) {
            System.out.println("Task " + i);
            if (IGNORED.contains(i)) {
                System.out.println("IGNORED");
                continue;
            }
            String taskName = String.format("%03d.json", i);
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
            if (lambdaMan.figure.vertices.size() * lambdaMan.figure.edges.size() > 3000) {
                System.out.println("TOO LARGE");
                continue;
            }
            AutoKutuzoffStrategy strategy = new AutoKutuzoffStrategy();
            state.applyStrategy(strategy);
            Figure figure = state.getMan().figure;
            boolean correct = ScoringUtils.checkFigure(figure, lambdaMan.figure, lambdaMan.epsilon);
            if (correct) {
                correctValues += 1;
            }
            boolean fits = ScoringUtils.fitsWithinHole(figure, state.getHole());
            if (fits) {
                fitted += 1;
            }
            if (correct && fits) {
                solved += 1;
                Pose pose = Pose.fromVertices(state.getMan().figure.vertices);
                String json = objectMapper.writeValueAsString(pose);
                Path path = Path.of("solutions", taskName);
                Files.writeString(path, json);
            }
            System.out.println("Correct " + correct + "; Fits " + fits);
        }
        System.out.println("Total correct " + correctValues + "; Total fits " + fitted + "; Total solved " + solved);
    }
}
