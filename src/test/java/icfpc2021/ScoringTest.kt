package icfpc2021

import icfpc2021.model.Edge
import icfpc2021.model.Figure
import icfpc2021.model.Hole
import icfpc2021.model.Vertex
import org.junit.Assert
import org.junit.Test

class ScoringTest {
    @Test
    fun testFitsCompletely() {
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
        val hole = Hole(
            listOf(
                Vertex(-1.0, -1.0),
                Vertex(2.0, -1.0),
                Vertex(2.0, 2.0),
                Vertex(-1.0, 2.0),
            ),
        )
        Assert.assertTrue(ScoringUtils.fitsWithinHole(figure, hole))
    }

    @Test
    fun testDoesntFitCompletely() {
        val figure = Figure(
            listOf(
                Vertex(-1.0, -1.0),
                Vertex(2.0, -1.0),
                Vertex(2.0, 2.0),
                Vertex(-1.0, 2.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        val hole = Hole(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(1.0, 0.0),
                Vertex(1.0, 1.0),
                Vertex(0.0, 1.0),
            ),
        )
        Assert.assertFalse(ScoringUtils.fitsWithinHole(figure, hole))
    }

    @Test
    fun testFitsPrecisely() {
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
        val hole = Hole(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(1.0, 0.0),
                Vertex(1.0, 1.0),
                Vertex(0.0, 1.0),
            ),
        )
        Assert.assertTrue(ScoringUtils.fitsWithinHole(figure, hole))
    }

    @Test
    fun testFitsLine012() {
        val hole = Hole(listOf(Vertex(28.0, 0.0), Vertex(56.0, 4.0), Vertex(0.0, 4.0)))
        val figure = Figure(listOf(Vertex(8.0, 36.0), Vertex(28.0, 16.0), Vertex(28.0, 16.0), Vertex(48.0, -4.0)),
        listOf(Edge(0, 1), Edge(0, 2), Edge(1, 3), Edge(2, 3)))
        Assert.assertFalse(ScoringUtils.fitsWithinHole(figure, hole))
    }

    @Test
    fun testDoesntFitSlightly() {
        val figure = Figure(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(1.0, 0.0),
                Vertex(1.0, 1.1),
                Vertex(0.0, 1.0),
            ),
            listOf(
                Edge(0, 1),
                Edge(1, 2),
                Edge(2, 3),
                Edge(3, 0),
            ),
        )
        val hole = Hole(
            listOf(
                Vertex(0.0, 0.0),
                Vertex(1.0, 0.0),
                Vertex(1.0, 1.0),
                Vertex(0.0, 1.0),
            ),
        )
        Assert.assertFalse(ScoringUtils.fitsWithinHole(figure, hole))
    }
}