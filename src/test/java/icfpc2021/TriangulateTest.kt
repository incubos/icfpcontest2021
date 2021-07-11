package icfpc2021

import icfpc2021.geom.Triangulate
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

}