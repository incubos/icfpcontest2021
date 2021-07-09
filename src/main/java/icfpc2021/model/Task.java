package icfpc2021.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class Task {
    public Hole hole;
    public Figure figure;
    public int epsilon;

    public Task(Hole hole, Figure figure, int epsilon) {
        this.hole = hole;
        this.figure = figure;
        this.epsilon = epsilon;
    }

    static Task fromRaw(RawTask rawTask) {
        Hole hole = new Hole();
        hole.vertices = rawTask.hole.stream().map(o -> new Vertex(o.get(0), o.get(1))).collect(Collectors.toList());
        Figure figure = new Figure();
        figure.edges = rawTask.figure.edges.stream().map(o -> new Edge(o.get(0), o.get(1))).collect(Collectors.toList());
        figure.vertices = rawTask.figure.vertices.stream().map(o -> new Vertex(o.get(0), o.get(1))).collect(Collectors.toList());
        return new Task(hole, figure, rawTask.epsilon);
    }

    public static Task fromJsonFile(Path path) throws IOException {
        var mapper = new ObjectMapper();
        var rawTask = mapper.readValue(Files.readString(path), RawTask.class);
        return Task.fromRaw(rawTask);
    }
}
