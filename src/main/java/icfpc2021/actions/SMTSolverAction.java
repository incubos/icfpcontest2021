package icfpc2021.actions;

import icfpc2021.geom.TriangleIdx;
import icfpc2021.model.Edge;
import icfpc2021.model.Figure;
import icfpc2021.model.Vertex;
import icfpc2021.viz.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Uses external solver.
 */
public class SMTSolverAction implements Action {
    private static final Logger log = LoggerFactory.getLogger(SMTSolverAction.class);

    private static String buildSMT(final State state) {
        final StringBuilder builder = new StringBuilder();
        try {
            builder.append(Files.readString(Paths.get(SMTSolverAction.class.getResource("/header.smt").toURI())));

            builder.append("; Hole\n");
            for (int i = 0; i < state.getHole().vertices.size(); i++) {
                final Vertex v = state.getHole().vertices.get(i);
                builder.append(String.format("(define-fun h%dx () Int %d)\n", i, Math.round(v.x)));
                builder.append(String.format("(define-fun h%dy () Int %d)\n", i, Math.round(v.y)));
            }

            builder.append("; Vertices\n");
            for (int i = 0; i < state.getOriginalMan().figure.vertices.size(); i++) {
                builder.append(String.format("(declare-const v%dx Int)\n", i));
                builder.append(String.format("(declare-const v%dy Int)\n", i));
            }

            builder.append("; Edge lengths\n");
            for (int i = 0; i < state.getOriginalMan().figure.edges.size(); i++) {
                final Edge edge = state.getOriginalMan().figure.edges.get(i);
                final Vertex start = state.getOriginalMan().figure.vertices.get(edge.start);
                final long startX = Math.round(start.x);
                final long startY = Math.round(start.y);
                final Vertex end = state.getOriginalMan().figure.vertices.get(edge.end);
                final long endX = Math.round(end.x);
                final long endY = Math.round(end.y);
                final long dx = endX - startX;
                final long dy = endY - startY;
                final long squareLength = dx * dx + dy * dy;

                builder.append(
                        String.format(
                                "(assert (= %d (squareLength v%dx v%dy v%dx v%dy) ))\n",
                                squareLength,
                                edge.start,
                                edge.start,
                                edge.end,
                                edge.end));
            }

            builder.append("; Inside hole\n");
            final List<TriangleIdx> triangles = state.getHoleTriangulation();
            for (int v = 0; v < state.getOriginalMan().figure.vertices.size(); v++) {
                final List<String> clauses = new ArrayList<>(triangles.size());
                for (final TriangleIdx triangle : triangles) {
                    clauses.add(String.format(
                            "(insideTriangle h%dx h%dy h%dx h%dy h%dx h%dy v%dx v%dy)\n",
                            triangle.component1(), triangle.component1(),
                            triangle.component2(), triangle.component2(),
                            triangle.component3(), triangle.component3(),
                            v, v));
                }
                builder.append(String.format("(assert %s)\n", or(clauses)));
            }

            builder.append("; Solve\n");
            builder.append("(check-sat)");

            builder.append("; Print\n");
            for (int i = 0; i < state.getOriginalMan().figure.vertices.size(); i++) {
                builder.append(String.format("(eval v%dx)\n", i));
                builder.append(String.format("(eval v%dy)\n", i));
            }

            return builder.toString();
        } catch (Exception e) {
            throw new RuntimeException("Can't build SMT", e);
        }
    }

    private static String or(final Collection<String> clauses) {
        if (clauses.isEmpty()) {
            throw new IllegalArgumentException("No clauses");
        }

        final Deque<String> ored = new LinkedList<>(clauses);
        while (ored.size() > 1) {
            final String left = ored.removeFirst();
            final String right = ored.removeFirst();
            final String result = String.format("(or %s %s)", left, right);
            ored.addLast(result);
        }

        return ored.iterator().next();
    }

    @Override
    public Figure apply(final State state, final Figure figure) {
        final String smt;
        try {
            smt = buildSMT(state);
        } catch (Exception e) {
            log.error("Can't build SMT model", e);
            return figure;
        }

        log.debug("SMT:\n" + smt);

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("z3", "sat.threads=4", "smt.threads=4", "sat.local_search_threads=4", "-in");
            builder.redirectError(new File("build/z3.stderr"));

            final Process process = builder.start();
            process.getOutputStream().write(smt.getBytes(StandardCharsets.UTF_8));
            process.getOutputStream().close();

            // TODO: Should we terminate the process?
            if (!process.waitFor(1, TimeUnit.MINUTES)) {
                log.error("SAT solver timed out");
                process.destroyForcibly();
                return figure;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                final String line = reader.readLine();
                if (!"sat".equals(line)) {
                    log.error("Can't solve");
                    return figure;
                }

                // Read vertices
                final List<Vertex> vertices = new ArrayList<>(figure.vertices.size());
                for (int i = 0; i < figure.vertices.size(); i++) {
                    final long x = Long.parseLong(reader.readLine());
                    final long y = Long.parseLong(reader.readLine());
                    vertices.add(new Vertex(x, y));
                }

                log.info("SAT solver provided the solution");

                return new Figure(vertices, figure.edges);
            }
        } catch (Exception e) {
            log.error("Can't run Z3", e);
            return figure;
        }
    }
}
