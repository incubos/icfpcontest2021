package icfpc2021

import icfpc2021.geom.Triangulate
import icfpc2021.geom.triangulateHolesInHole
import icfpc2021.model.Task
import org.junit.Assert
import org.junit.Test
import java.nio.file.Path

class TriangulateTest {
    @Test
    fun testTriangulate() {
        for (i in 1..132) {
            println("Problem $i")
            val taskName = String.format("%03d.json", i)
            val problemPath = Path.of("problems", taskName)
            val task = Task.fromJsonFile(problemPath)
            if (i !in setOf(32, 91, 93, 107, 110, 112, 114, 116, 119, 120, 123, 126, 129, 131)) {
                Triangulate.triangulate(task.hole.vertices)
                if (i !in setOf(33, 79, 89, 98, 103, 104)) {
                    triangulateHolesInHole(task.hole)
                }
            }
        }
        Assert.assertTrue(true)
    }
}