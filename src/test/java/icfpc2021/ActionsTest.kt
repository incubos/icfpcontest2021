package icfpc2021

import icfpc2021.actions.MoveAction
import icfpc2021.actions.RotateAction
import icfpc2021.model.Edge
import icfpc2021.model.Figure
import icfpc2021.model.Vertex
import org.junit.Assert
import org.junit.Test

class ActionsTest {
    @Test
    fun testMoveEmpty() {
        val figure = Figure(listOf(), listOf())
        val action = MoveAction(1.0, 1.0)
        val actual = action.apply(figure)
        val expected = Figure(listOf(), listOf())
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testMoveSquare1() {
        val figure = Figure(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(1.0, 0.0),
                Vertex(1.0, 1.0),
                Vertex(0.0, 1.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        val action = MoveAction(1.0, 1.0)
        val actual = action.apply(figure)
        val expected = Figure(
            listOf(
                Vertex(1.0, 1.0),
                Vertex(2.0, 1.0),
                Vertex(2.0, 2.0),
                Vertex(1.0, 2.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun testRotateRect21() {
        val figure = Figure(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(2.0, 0.0),
                Vertex(2.0, 1.0),
                Vertex(0.0, 1.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        val action = RotateAction(0.0, 0.0, 90.0)
        val actual = action.apply(figure)
        val expected = Figure(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(0.0, 2.0),
                Vertex(-1.0, 2.0),
                Vertex(-1.0, 0.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        Assert.assertEquals(expected, actual)
    }
}