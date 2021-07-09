package icfpc2021.viz

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class Field(val state: State) : JPanel() {
    override fun paint(g: Graphics) {
        val g2d = g as Graphics2D
        drawField(g2d)
        drawControls(g2d)
    }

    private fun drawControls(g2d: Graphics2D) {
        val hole = state.hole
        val man = state.man
    }

    private fun drawField(g2d: Graphics2D) {
        val hole = state.hole
        val man = state.man

        g2d.color = Color.LIGHT_GRAY
        g2d.fillRect(0, 0, width, height)
        // Draw hole
        g2d.color = Color.WHITE
        g2d.fillPolygon(
            hole.vertices.map { screenX(it.x) }.toIntArray(),
            hole.vertices.map { screenY(it.y) }.toIntArray(),
            hole.vertices.size
        )
        g2d.color = Color.BLACK
        g2d.stroke = BasicStroke(2f)
        hole.vertices.forEachIndexed { i, v1 ->
            val v2 = hole.vertices[(i + 1).mod(hole.vertices.size)]
            g2d.drawLine(screenX(v1.x), screenY(v1.y), screenX(v2.x), screenY(v2.y))
        }

        // Draw man
        g2d.color = Color.RED
        g2d.stroke = BasicStroke(5f)
        man.figure.edges.forEach { e ->
            val p1 = man.figure.vertices[e.start]
            val p2 = man.figure.vertices[e.end]
            g2d.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
        }
    }


    private fun screenX(x: Double) = (M + (width - M * 2) * (x - state.minX()) / (state.maxX() - state.minX())).toInt()
    private fun screenY(y: Double) = (M + (height - M * 2) * (y - state.minY()) / (state.maxY() - state.minY())).toInt()

    val M = 100

}