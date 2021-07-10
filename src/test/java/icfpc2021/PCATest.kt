package icfpc2021

import icfpc2021.geom.principalComponents
import icfpc2021.model.Vertex
import org.junit.Assert
import org.junit.Test

class PCATest {
    @Test
    fun test() {
        val vectors = principalComponents(
            listOf(
                Vertex(0.0, -10.0),
                Vertex(5.0, 0.0),
                Vertex(0.0, 10.0),
                Vertex(-5.0, 0.0),
            )
        )

        Assert.assertEquals(listOf(Vertex(0.0, 1.0), Vertex(1.0, 0.0)), vectors)
    }
}