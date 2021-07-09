package icfpc2021.viz

import icfpc2021.actions.MoveAction
import icfpc2021.actions.PushVertexAction
import icfpc2021.actions.RotateAction
import icfpc2021.model.LambdaMan
import java.awt.*
import java.awt.event.*
import javax.swing.*

class Field(val state: State) : JPanel() {

    fun position(e: MouseEvent): String {
        val realX = realX(e.x)
        val realY = realY(e.y)
        val manVertex = state.findVertex(state.man.figure.vertices, realX, realY)
        val holeVertex = state.findVertex(state.hole.vertices, realX, realY)
        return "[$realX,$realY][Closest man:$manVertex][Closest hole:$holeVertex"
    }

    fun addActionsListener(actionsPanel: ActionsPanel) {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val realX = realX(e.x)
                val realY = realY(e.y)
                actionsPanel.status.text = "$state${position(e)} Mouse click"

                if (state.actionInProcess == MoveAction::class.simpleName) {
                    finishMoveAction(actionsPanel, realX, realY)
                    return
                }

                if (state.actionInProcess == PushVertexAction::class.simpleName) {
                    finishPushMoveAction(actionsPanel, realX, realY)
                    return
                }

                val manVertex = state.findVertex(state.man.figure.vertices, realX, realY)
                when (manVertex) {
                    null -> {
                        actionsPanel.disableButtons()
                        state.selectedVertex = null
                        actionsPanel.status.text = "$state${position(e)} No vertices selected"
                    }
                    state.selectedVertex -> {
                        actionsPanel.disableButtons()
                        state.selectedVertex = null
                        actionsPanel.status.text = "$state${position(e)} Vertex man $manVertex deselected"
                    }
                    else -> {
                        actionsPanel.enableButtons()
                        state.selectedVertex = manVertex
                        actionsPanel.status.text = "$state${position(e)} Vertex man $manVertex selected"
                    }
                }
                repaint()
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                actionsPanel.status.text = "$state${position(e)}"
            }
        })

        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                keyPressed(e, actionsPanel)
            }
        })

        actionsPanel.moveButton.addActionListener {
            actionsPanel.status.text = "$state Select point to move"
            state.actionInProcess = MoveAction::class.simpleName
            actionsPanel.disableButtons()
        }

        actionsPanel.rotateButton.addActionListener {
            finishRotateAction(actionsPanel)
        }

        actionsPanel.pushVertexButton.addActionListener {
            actionsPanel.status.text = "$state Select point to push"
            state.actionInProcess = PushVertexAction::class.simpleName
            actionsPanel.disableButtons()
        }

    }

    fun keyPressed(e: KeyEvent, actionsPanel: ActionsPanel) {
        if (e.keyCode == 27) { // Escape
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableButtons()
            actionsPanel.status.text = "$state${state.printMan()}"
            repaint()
        }
    }

    private fun finishRotateAction(actionsPanel: ActionsPanel) {
        actionsPanel.status.text = "$state Enter degrees"
        state.actionInProcess = RotateAction::class.simpleName
        actionsPanel.disableButtons()

        // Show input dialog
        val textComponent = JTextArea("")
        val optPane = JOptionPane(JPanel(BorderLayout()).apply {
            add(JLabel("Enter degrees"), BorderLayout.NORTH)
            add(JScrollPane(textComponent), BorderLayout.CENTER)
        }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
        optPane.createDialog(this, "Rotation").apply {
            isVisible = true
        }
        if (optPane.value == JOptionPane.OK_OPTION) {
            val degrees = textComponent.text.toInt()
            val v = state.man.figure.vertices[state.selectedVertex!!]
            val action = RotateAction(v.x, v.y, degrees.toDouble())
            val newFigure = action.apply(state.man.figure)
            state.actions.add(action)
            state.figures.add(newFigure)
            state.man = LambdaMan().apply {
                figure = newFigure
                epsilon = state.man.epsilon
            }
        }
        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.status.text = "$state Rotated successfully"
        repaint()
    }

    private fun finishMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val v = state.man.figure.vertices[state.selectedVertex!!]
        val action = MoveAction(realX - v.x, realY - v.y)
        val newFigure = action.apply(state.man.figure)
        state.actions.add(action)
        state.figures.add(newFigure)
        state.man = LambdaMan().apply {
            figure = newFigure
            epsilon = state.man.epsilon
        }

        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.status.text = "$state Moved successfully"
        repaint()
    }

    private fun finishPushMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val v = state.man.figure.vertices[state.selectedVertex!!]
        val action = PushVertexAction(state.selectedVertex!!, (realX - v.x).toInt(), (realY - v.y).toInt())
        val newFigure = action.apply(state.man.figure)
        state.actions.add(action)
        state.figures.add(newFigure)
        state.man = LambdaMan().apply {
            figure = newFigure
            epsilon = state.man.epsilon
        }

        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.status.text = "$state PushVertex successfully"
        repaint()
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
            g2d.drawOval(screenX(v.x) - 5, screenY(v.y) - 5, 10, 10)
        }
        g2d.color = Color.BLACK
        man.figure.vertices.forEachIndexed { i, v ->
            g2d.drawString(i.toString(), screenX(v.x).toFloat(), screenY(v.y).toFloat())
        }
    }

    // Screen and model coordinates conversion
    private fun screenX(x: Double) =
        (MARGIN + (width - MARGIN * 2) * (x - state.minX()) / (state.maxX() - state.minX())).toInt()

    private fun screenY(y: Double) =
        (MARGIN + (height - MARGIN * 2) * (y - state.minY()) / (state.maxY() - state.minY())).toInt()

    private fun realX(xScreen: Int) =
        state.minX() + (xScreen - MARGIN) * (state.maxX() - state.minX()) / (width - MARGIN * 2)

    private fun realY(yScreen: Int) =
        state.minY() + (yScreen - MARGIN) * (state.maxY() - state.minY()) / (height - MARGIN * 2)

    val MARGIN = 10
}