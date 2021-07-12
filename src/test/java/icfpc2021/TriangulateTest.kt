package icfpc2021

import icfpc2021.ScoringUtils.edgeIntersectsTriangle
import icfpc2021.geom.Triangulate
import icfpc2021.geom.Triangulate.Companion.pointInTriangle
import icfpc2021.geom.triangulateHolesInHole
import icfpc2021.model.Hole
import icfpc2021.model.Task
import icfpc2021.model.Vertex
import org.junit.Assert
import org.junit.Test
import java.nio.file.Path

class TriangulateTest {

    companion object {
        val TASKS = 132
    }

    @Test
    fun testTriangulate() {
        for (i in 1..TASKS) {
            println("Problem $i")
            val taskName = String.format("%03d.json", i)
            val problemPath = Path.of("problems", taskName)
            val task = Task.fromJsonFile(problemPath)
            Triangulate.triangulate(task.hole.vertices)
        }
        Assert.assertTrue(true)
    }

    @Test
    fun testTriangulateHoles() {
        for (i in 1..TASKS) {
            println("Problem $i")
            val taskName = String.format("%03d.json", i)
            val problemPath = Path.of("problems", taskName)
            val task = Task.fromJsonFile(problemPath)
            triangulateHolesInHole(task.hole)
        }
        Assert.assertTrue(true)
    }

    @Test
    fun testHoles19() {
        val vertices = listOf(
            Vertex(58.0, 56.0),
            Vertex(57.0, 46.0),
            Vertex(55.0, 37.0),
            Vertex(54.0, 27.0),
            Vertex(58.0, 19.0),
            Vertex(49.0, 16.0),
            Vertex(58.0, 0.0)
        )
        triangulateHolesInHole(Hole(vertices))
    }

    // Point on the edge
    @Test
    fun testInTriangle() {
        val p = Vertex(12.0, 49.0)
        val a = Vertex(43.0, 14.0)
        val b = Vertex(19.0, 52.0)
        val c = Vertex(7.0, 53.0)
        Assert.assertTrue(pointInTriangle(p, a, b, c))
    }

    // Task 42 Figure egde 0, 1 intersects with outer boundary 13, 15, 14
    @Test
    fun testEdgeIntersectsTriangle() {
        val x1 = Vertex(26.0, 21.0) // 0
        val x2 = Vertex(48.0, 10.0) // 1
        val a = Vertex(7.0, 53.0) // 13
        val b = Vertex(0.0, 43.0) // 15
        val c = Vertex(11.0, 40.0) // 14
        Assert.assertFalse(
            edgeIntersectsTriangle(
                a.x, a.y, b.x, b.y, c.x, c.y,
                x1.x, x1.y, x2.x, x2.y
            )
        )
    }
    // Task 42 After Kutuzoff Figure edge 2, 6 intersects with outer boundary 2, 5, 4
    @Test
    fun testEdgeIntersectsTriangle2() {
        val x1 = Vertex(11.0, 8.0) // 2
        val x2 = Vertex(49.0, 36.0) // 6
        val a = Vertex(34.0, 0.0) // 2
        val b = Vertex(48.0, 0.0) // 5
        val c = Vertex(43.0, 15.0) // 4
        Assert.assertFalse(
            edgeIntersectsTriangle(
                a.x, a.y, b.x, b.y, c.x, c.y,
                x1.x, x1.y, x2.x, x2.y
            )
        )
    }
    // Figure edge 1(6.0, 0.0), 3(36.0, 8.0) intersects with outer boundary 2(34.0, 43.0), 4(43.0, 43.0), 3(36.0, 36.0)
    @Test
    fun testEdgeIntersectsTriangle3() {
        val x1 = Vertex(6.0, 0.0)
        val x2 = Vertex(36.0, 0.0)
        val a = Vertex(34.0, 43.0)
        val b = Vertex(43.0, 43.0)
        val c = Vertex(36.0, 36.0)
        Assert.assertFalse(
            edgeIntersectsTriangle(
                a.x, a.y, b.x, b.y, c.x, c.y,
                x1.x, x1.y, x2.x, x2.y
            )
        )
    }

}
