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

import icfpc2021.model.Edge;
import icfpc2021.model.Task;
import icfpc2021.model.Vertex;
import org.dyn4j.collision.AxisAlignedBounds;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Vector2;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        g.fillPolygon(task.hole.vertices.stream().map(o -> o.x).map(o->this.camera.scale*o).mapToInt(o -> (int) o.intValue()).toArray(),
                task.hole.vertices.stream().map(o -> o.y).map(o->this.camera.scale*o).mapToInt(o -> (int) o.intValue()).toArray(),
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
        Map<Integer,SimulationBody> bodies = new HashMap<>();
        for (int i =0; i< task.figure.vertices.size();i++) {
            Vertex vertex = task.figure.vertices.get(i);
            SimulationBody body = new SimulationBody(i+"", Color.ORANGE);
            body.addFixture(Geometry.createCircle(0.25));
            body.setMass(MassType.NORMAL);
            body.translate(new Vector2(vertex.x, vertex.y));
            updateMass(body);
            world.addBody(body);
            bodies.put(i,body);
        }


        for (Edge edge : task.figure.edges) {
            addJoint(bodies.get(edge.start),bodies.get(edge.end), task.epsilon);
        }

        world.setBounds(new AxisAlignedBounds(1000, 1000));

//        SimulationBody d1 = new SimulationBody("d1", Color.BLUE);
//        d1.addFixture(Geometry.createCircle(0.25));
//        d1.setMass(MassType.NORMAL);
//        d1.translate(new Vector2(0, 0));
//        updateMass(d1);
//        world.addBody(d1);
//
//        SimulationBody d2 = new SimulationBody("d2", Color.BLACK);
//        d2.addFixture(Geometry.createCircle(0.25));
//        d2.translate(new Vector2(1, 0));
//        d2.setMass(MassType.NORMAL);
//        updateMass(d2);
//        world.addBody(d2);
//
//
//        SimulationBody d3 = new SimulationBody("d3", Color.GREEN);
//        d3.addFixture(Geometry.createCircle(0.25));
//        d3.translate(new Vector2(1, 1));
//        d3.setMass(MassType.NORMAL);
//        updateMass(d3);
//        world.addBody(d3);
//
//        addJoint(d1, d2, 0.25);
//        addJoint(d2, d3, 0.25);
//        addJoint(d3, d1, 0.25);
    }

    public static int id = 0;

    private void addJoint(SimulationBody d1, SimulationBody d2, double epsilon) {
        IdRopeJoint d1d2 = new IdRopeJoint(id++, d1, d2, d1.getTransform().getTranslation(), d2.getTransform().getTranslation());
        //dNEW <= (e/1_000_000 +1) * d
        d1d2.setLowerLimit(Math.sqrt(d1d2.getAnchor1().distanceSquared(d1d2.getAnchor2()) * (1-(epsilon/1_000_000))));
        d1d2.setUpperLimit(Math.sqrt(d1d2.getAnchor1().distanceSquared(d1d2.getAnchor2()) * (1+(epsilon/1_000_000))));
        world.addJoint(d1d2);
    }

    ConcurrentHashMap<Integer, Double> joints = new ConcurrentHashMap<Integer, Double>();

    @Override
    protected void handleEvents() {
        super.handleEvents();
        Graphics2D graphics2D = getGraphics2D();
        Font helvetica = new Font("Helvetica", Font.BOLD, 24);
        graphics2D.setFont(helvetica);
        if (printKeyPressed.compareAndSet(true, false)) {
            for (SimulationBody body : world.getBodies()) {
                System.out.println(body.name + " : " + "x: " + body.getTransform().getTranslation().x + ", y: " + body.getTransform().getTranslation().y);
            }
        }
        if (elasticRopes.compareAndSet(true, false)) {
            for (Joint<SimulationBody> joint : world.getJoints()) {
                if (!(joint instanceof IdRopeJoint)) {
                    continue;
                }
                IdRopeJoint rj = (IdRopeJoint) joint;
                joints.put(rj.id, rj.getLowerLimit());
                rj.setLowerLimit(0);
            }
        }
        if (rigidRopes.compareAndSet(true, false)) {
            for (Joint<SimulationBody> joint : world.getJoints()) {
                if (!(joint instanceof IdRopeJoint)) {
                    continue;
                }
                IdRopeJoint rj = (IdRopeJoint) joint;
                Double lowerLimit = joints.get(rj.id);
                if (lowerLimit != null) {
                    rj.setLowerLimit(lowerLimit);
                }
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
