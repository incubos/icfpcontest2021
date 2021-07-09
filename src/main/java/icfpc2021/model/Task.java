package icfpc2021.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
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
        var holeVertices = rawTask.hole.stream().map(o -> new Vertex(o.get(0), o.get(1))).collect(Collectors.toList());
        Hole hole = new Hole(holeVertices);
        var edges = rawTask.figure.edges.stream().map(o -> new Edge(o.get(0), o.get(1))).collect(Collectors.toList());
        var vertices = rawTask.figure.vertices.stream().map(o -> new Vertex(o.get(0), o.get(1))).collect(Collectors.toList());
        var figure = new Figure(vertices, edges);
        return new Task(hole, figure, rawTask.epsilon);
    }

    public static Task fromJsonFile(Path path) throws IOException {
        var mapper = new ObjectMapper();
        var rawTask = mapper.readValue(Files.readString(path), RawTask.class);
        return Task.fromRaw(rawTask);
    }
}
