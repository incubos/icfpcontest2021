/*
 * Copyright (c) 2010-2016 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 *     and the following disclaimer in the documentation and/or other materials provided with the
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or
 *     promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package icfpc2021.physics.framework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import icfpc2021.ScoringUtils;
import icfpc2021.actions.*;
import icfpc2021.model.*;
import icfpc2021.strategy.PosifyEdges;
import icfpc2021.viz.State;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.AbstractPhysicsBody;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.PinJoint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A somewhat complex scene with a ragdoll.
 *
 * @author William Bittle
 * @version 3.2.0
 * @since 3.2.1
 */
public class Ragdoll extends SimulationFrame {
    /**
     * The serial version id
     */
    private static final long serialVersionUID = -2350301592218819726L;
    public static final double DAMPING = Double.MAX_VALUE;

    AtomicBoolean printKeyPressed = new AtomicBoolean(false);
    AtomicBoolean elasticRopes = new AtomicBoolean(false);
    AtomicBoolean rigidRopes = new AtomicBoolean(false);
    AtomicBoolean superRigidRopes = new AtomicBoolean(false);
    AtomicBoolean pacifyButton = new AtomicBoolean(false);

    Task task;
    Path problemPath;
    String name;

    private class CustomKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P:
                    printKeyPressed.set(true);
                    break;
                case KeyEvent.VK_E:
                    System.out.println(elasticRopes.get());
                    elasticRopes.set(true);
                    break;
                case KeyEvent.VK_R:
                    System.out.println(rigidRopes.get());
                    rigidRopes.set(true);
                    break;
                case KeyEvent.VK_S:
                    System.out.println(superRigidRopes.get());
                    superRigidRopes.set(true);
                    break;
                case KeyEvent.VK_A:
                    System.out.println(pacifyButton.get());
                    pacifyButton.set(!pacifyButton.get());
                    break;
            }

        }
    }

    /**
     * Default constructor.
     */
    public Ragdoll(Task task, Path problemPath, String name) {
        super("Ragdoll", 64.0);
        this.task = task;
        this.problemPath = problemPath;
        this.name = name;
        KeyListener listener = new CustomKeyListener();
        this.addKeyListener(listener);
        this.canvas.addKeyListener(listener);
        this.initializeWorld();
    }


    @Override
    protected void render(Graphics2D g, double elapsedTime) {
        Color color = g.getColor();
        g.setColor(Color.GRAY);
        g.fillPolygon(task.hole.vertices.stream().map(o -> o.x).map(o -> this.camera.scale * o).mapToInt(o -> (int) o.intValue()).toArray(),
                task.hole.vertices.stream().map(o -> o.y).map(o -> this.camera.scale * o).mapToInt(o -> (int) o.intValue()).toArray(),
                task.hole.vertices.size());
        g.setColor(color);
        super.render(g, elapsedTime);
    }

    private void updateMass(SimulationBody body) {
        body.setLinearDamping(DAMPING);
        body.setAngularDamping(DAMPING);
        body.setGravityScale(0);
    }

    /**
     * Creates game objects and adds them to the world.
     */
    protected void initializeWorld() {


        // the ragdoll

        // Head
        Map<Integer, SimulationBody> bodies = new HashMap<>();
        for (int i = 0; i < task.figure.vertices.size(); i++) {
            Vertex vertex = task.figure.vertices.get(i);
            SimulationBody body = new SimulationBody(i, Color.ORANGE);
            body.addFixture(Geometry.createCircle(0.5));
            body.setMass(MassType.NORMAL);
            body.translate(new Vector2(vertex.x, vertex.y));
            updateMass(body);
            world.addBody(body);
            bodies.put(i, body);
        }
        for (Edge edge : task.figure.edges) {
            addJoint(bodies.get(edge.start), bodies.get(edge.end), task.epsilon);
        }

        world.setBounds(new AxisAlignedBounds(1000, 1000));
    }

    public static int id = 0;
    public static ObjectMapper objectMapper = new ObjectMapper();

    private void addJoint(SimulationBody d1, SimulationBody d2, double epsilon) {
        IdRopeJoint d1d2 = new IdRopeJoint(id++, d1, d2, d1.getTransform().getTranslation(), d2.getTransform().getTranslation());
        //dNEW <= (e/1_000_000 +1) * d
        d1d2.setLowerLimit(Math.sqrt(d1d2.getAnchor1().distanceSquared(d1d2.getAnchor2()) * (1 - (epsilon / 1_000_000))));
        d1d2.setUpperLimit(Math.sqrt(d1d2.getAnchor1().distanceSquared(d1d2.getAnchor2()) * (1 + (epsilon / 1_000_000))));
        lowerJoints.put(d1d2.id, d1d2.getLowerLimit());
        upperJoints.put(d1d2.id, d1d2.getUpperLimit());
        strictJoints.put(d1d2.id, d1d2.getAnchor1().distance(d1d2.getAnchor2()));
        world.addJoint(d1d2);
    }

    ConcurrentHashMap<Integer, Double> lowerJoints = new ConcurrentHashMap<Integer, Double>();
    ConcurrentHashMap<Integer, Double> upperJoints = new ConcurrentHashMap<Integer, Double>();
    ConcurrentHashMap<Integer, Double> strictJoints = new ConcurrentHashMap<Integer, Double>();


    private void posify(List<Vertex> vertices) {
        Figure figure = new Figure(vertices, this.task.figure.edges);
        LambdaMan lambdaMan = new LambdaMan();
        lambdaMan.figure = this.task.figure;
        lambdaMan.epsilon = this.task.epsilon;

        figure = new FullPosifyAction().doApply(figure,this.task.hole);

        figure = new PosifyAction().doApply(figure, this.task.figure);

        for (MoveVertexToGridAction action : new PosifyEdges().doApply(figure,State.Companion.computeAdjacencyList(task.figure), lambdaMan )) {
            figure = action.doApply(figure);
        }

        boolean correct = ScoringUtils.checkFigure(figure, lambdaMan.figure, lambdaMan.epsilon);
        boolean inHole = ScoringUtils.fitsWithinHole(figure, this.task.hole);
        boolean inGrid = ScoringUtils.isFigureInGrid(figure);
        System.out.println("Solution " + name);
        final double[] originalSquareLengths = ScoringUtils.edgeSquareLengthsFrom(lambdaMan.figure.vertices, lambdaMan.figure.edges);
        System.out.println("Original lengths: " + Arrays.toString(originalSquareLengths));
        final double[] ourSquareLengths = ScoringUtils.edgeSquareLengthsFrom(figure.vertices, figure.edges);
        System.out.println("Our lengths: " + Arrays.toString(ourSquareLengths));
        final int[] epsilons = new int[figure.edges.size()];
        for (int a = 0; a < epsilons.length; a++) {
            epsilons[a] = (int) Math.ceil(Math.abs(ourSquareLengths[a] / originalSquareLengths[a] - 1.0) * 1_000_000);
        }
        System.out.println("Epsilons: " + Arrays.toString(epsilons));
        Path solutionPath = Path.of("solutions", name);
        if (correct && inHole && inGrid) {
            Pose pose = Pose.fromVertices(figure.vertices);
            String json = null;
            try {
                json = objectMapper.writeValueAsString(pose);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                Files.writeString(solutionPath, json);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("Correct " + correct + "; Fits " + inHole + "; In grid " + inGrid);
    }

    Set<Joint<SimulationBody>> pinJoints = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    protected void handleEvents() {
        super.handleEvents();
        Graphics2D graphics2D = getGraphics2D();
        Font helvetica = new Font("Helvetica", Font.BOLD, 24);
        graphics2D.setFont(helvetica);
        if (printKeyPressed.compareAndSet(true, false)) {
            Map<Integer, Vertex> vertices = new TreeMap<Integer, Vertex>();
            for (SimulationBody body : world.getBodies()) {
                vertices.put(body.name, new Vertex(body.getTransform().getTranslation().x, body.getTransform().getTranslation().y));
                System.out.println(body.name + " : " + "x: " + body.getTransform().getTranslation().x + ", y: " + body.getTransform().getTranslation().y);
            }
            List<Vertex> collect = vertices.entrySet().stream().sorted(Comparator.comparing(o -> o.getKey())).map(o -> o.getValue()).collect(Collectors.toList());
            posify(collect);
        }
        if (elasticRopes.compareAndSet(true, false)) {
            for (Joint<SimulationBody> joint : world.getJoints()) {
                if (!(joint instanceof IdRopeJoint)) {
                    continue;
                }
                IdRopeJoint rj = (IdRopeJoint) joint;
                rj.setLowerLimit(0);
            }
        }
        if (rigidRopes.compareAndSet(true, false)) {
            for (Joint<SimulationBody> joint : world.getJoints()) {
                if (!(joint instanceof IdRopeJoint)) {
                    continue;
                }
                IdRopeJoint rj = (IdRopeJoint) joint;
                Double lowerLimit = lowerJoints.get(rj.id);
                if (lowerLimit != null) {
                    rj.setLowerLimit(lowerLimit);
                }
            }
        }
        if (pacifyButton.get()) {
            if (world.getBodies().stream().allMatch(AbstractPhysicsBody::isAtRest)) {
                pinJoints.forEach(world::removeJoint);
            } else {
                for (SimulationBody body : world.getBodies()) {
                    Vertex thisVertex = new Vertex(body.getTransform().getTranslationX(), body.getTransform().getTranslationY());
                    List<Vertex> vertices = world.getBodies()
                            .stream()
                            .sorted(Comparator.comparing(o -> o.name))
                            .map(o -> {
                                return thisVertex;
                            }).collect(Collectors.toList());
                    Vertex round = ScoringUtils.round(body.name,
                            new Vertex(body.getTransform().getTranslationX(),
                                    body.getTransform().getTranslationY()),
                            vertices, task.figure.edges, task.figure);
                    PinJoint<SimulationBody> simulationBodyPinJoint = new PinJoint<>(body, new Vector2(thisVertex.x, thisVertex.y), 8.0, 0.2, 1000);
                    this.world.addJoint(simulationBodyPinJoint);
                    pinJoints.add(simulationBodyPinJoint);
                    simulationBodyPinJoint.setTarget(new Vector2(round.x, round.y));

                }
            }
        }
        if (superRigidRopes.compareAndSet(true, false)) {
            for (Joint<SimulationBody> joint : world.getJoints()) {
                if (!(joint instanceof IdRopeJoint)) {
                    continue;
                }
                IdRopeJoint rj = (IdRopeJoint) joint;
                Double lowerLimit = strictJoints.get(rj.id);
                rj.setLowerLimit(lowerLimit);
                rj.setUpperLimit(lowerLimit);
            }
        }
    }

    /**
     * Entry point for the example application.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            throw new RuntimeException("Usage: Visualize <path-to-problem-json>");
        }

        Path problemPath = Path.of(args[0]);
        Task task = Task.fromJsonFile(problemPath);
        String name = problemPath.getFileName().toString();

        Ragdoll simulation = new Ragdoll(task, problemPath, name);
        simulation.run();
    }
}
