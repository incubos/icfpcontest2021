package icfpc2021.viz

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel

class Field(val state: State) : JPanel() {

    fun addActionsListener(actionsPanel: ActionsPanel) {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val realX = realX(e.x)
                val realY = realY(e.y)
                actionsPanel.status.text = "Mouse click on ${e.x}, ${e.y} = $realX, $realY"
                val clickVertex = state.findVertex(state.man.figure.vertices, realX, realY)
                when (clickVertex) {
                    null -> {
                        actionsPanel.status.text = "No vertices selected"
                        actionsPanel.moveButton.isEnabled = false
                        actionsPanel.rotateButton.isEnabled = false
                        state.selectedVertex = null
                    }
                    state.selectedVertex -> {
                        actionsPanel.status.text = "Vertex $clickVertex deselected"
                        actionsPanel.moveButton.isEnabled = false
                        actionsPanel.rotateButton.isEnabled = false
                        state.selectedVertex = null
                    }
                    else -> {
                        actionsPanel.status.text = "Vertex $clickVertex selected"
                        actionsPanel.moveButton.isEnabled = true
                        actionsPanel.rotateButton.isEnabled = true
                        state.selectedVertex = clickVertex
                    }
                }
                repaint()
            }

            override fun mouseMoved(e: MouseEvent) {
                val realX = realX(e.x)
                val realY = realY(e.y)
                actionsPanel.status.text = "${e.x}, ${e.y} = $realX, $realY"
            }
        })
//         addKeyListener(object : KeyAdapter() {
//             override fun keyPressed(e: KeyEvent) {
//                 if (e.keyCode == 27) { // Escape
//                     actionsPanel.status.text = state.printMan()
//                     state.selectedVertex = null
//                 }
//             }
//         })
    }

    override fun paint(g: Graphics) {
        val g2d = g as Graphics2D
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
        if (state.selectedVertex != null) {
            g2d.color = Color.GREEN
            val v = man.figure.vertices[state.selectedVertex!!]
            g2d.drawOval(screenX(v.x), screenY(v.y), 5, 5)
        }
        g2d.color = Color.BLACK
        man.figure.vertices.forEachIndexed { i, v ->
            g2d.drawString(i.toString(), screenX(v.x).toFloat(), screenY(v.y).toFloat())
        }
    }


    private fun screenX(x: Double) = (M + (width - M * 2) * (x - state.minX()) / (state.maxX() - state.minX())).toInt()
    private fun screenY(y: Double) = (M + (height - M * 2) * (y - state.minY()) / (state.maxY() - state.minY())).toInt()

    private fun realX(xScreen: Int) = (xScreen - M) * (state.maxX() - state.minX()) / (width - M * 2)
    private fun realY(yScreen: Int) = (yScreen - M) * (state.maxY() - state.minY()) / (height - M * 2)

    val M = 100

}