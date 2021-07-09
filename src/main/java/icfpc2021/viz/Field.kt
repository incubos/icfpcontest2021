package icfpc2021.viz

import icfpc2021.ScoringUtils
import icfpc2021.actions.Action
import icfpc2021.actions.MoveAction
import icfpc2021.actions.PushVertexAction
import icfpc2021.actions.RotateAction
import java.awt.*
import java.awt.event.*
import javax.swing.*
import kotlin.math.roundToInt

class Field(val state: State) : JPanel() {

    fun position(e: MouseEvent): String {
        val realX = realX(e.x)
        val realY = realY(e.y)
        val manVertexIdx = state.findVertex(state.man.figure.vertices, realX, realY)
        val manVertex = if (manVertexIdx != null) state.man.figure.vertices[manVertexIdx] else null
        val holeVertexIdx = state.findVertex(state.hole.vertices, realX, realY)
        val holeVertex = if (holeVertexIdx != null) state.hole.vertices[holeVertexIdx] else null
        return "[x${realX.toInt()},y${realY.toInt()}]" +
                "[Closest man:${if (manVertex != null) "#$manVertexIdx(${manVertex.x.toInt()},${manVertex.y.toInt()})" else "-"}]" +
                "[Closest hole:${if (holeVertex != null) "#$holeVertexIdx(${holeVertex.x.toInt()},${holeVertex.y.toInt()})" else "-"}]"
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
            state.actionInProcess = MoveAction::class.simpleName
            actionsPanel.status.text = "$state Select point to move"
            actionsPanel.disableButtons()
        }

        actionsPanel.rotateButton.addActionListener {
            finishRotateAction(actionsPanel)
        }

        actionsPanel.pushVertexButton.addActionListener {
            state.actionInProcess = PushVertexAction::class.simpleName
            actionsPanel.status.text = "$state Select point to push"
            actionsPanel.disableButtons()
        }

        actionsPanel.rollBackLastAction.addActionListener {
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableButtons()
            if (state.actions.size == 0) {
                actionsPanel.status.text = "$state No actions to rollback"
            } else {
                actionsPanel.status.text = "$state Rolled back action ${state.actions.last().javaClass.simpleName}"
                state.actions.removeLast()
                state.figures.removeLast()
                state.current = state.figures.size - 1
                actionsPanel.status.text = "$state"
                repaint()
            }
        }

        actionsPanel.backButton.addActionListener {
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableButtons()
            if (state.current <= 0) {
                actionsPanel.status.text = "$state Nothing to apply"
            } else {
                state.current -= 1
                actionsPanel.status.text = "$state"
                repaint()
            }
        }


        actionsPanel.forwardButton.addActionListener {
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableButtons()
            if (state.current >= state.figures.size) {
                actionsPanel.status.text = "$state Nothing to apply"
            } else {
                state.current += 1
                actionsPanel.status.text = "$state"
                repaint()
            }
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
        val textComponent = JTextField("")
        val optPane = JOptionPane(JPanel(BorderLayout()).apply {
            add(JLabel("Enter degrees"), BorderLayout.NORTH)
            add(JScrollPane(textComponent), BorderLayout.CENTER)
        }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
        optPane.createDialog(this, "Rotation").apply {
            isVisible = true
        }
        if (optPane.value == JOptionPane.OK_OPTION) {
            val degrees = try {
                textComponent.text.toInt()
            } catch (e: Exception) {
                0
            }
            val v = state.man.figure.vertices[state.selectedVertex!!]
            val action = RotateAction(v.x, v.y, degrees.toDouble())
            val newFigure = action.apply(state.man.figure)
            state.actions.add(action)
            state.figures.add(newFigure)
            state.current = state.figures.size - 1
            actionsPanel.status.text = "$state Rotated to $degrees"
        }
        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.disableButtons()
        repaint()
    }

    private fun applyAction(action: Action, actionsPanel: ActionsPanel) {
        val newFigure = action.apply(state.man.figure)
        state.actions.add(action)
        state.figures.add(newFigure)
        state.current = state.figures.size - 1
        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.status.text = "$state ${action.javaClass.simpleName} successfully"
        repaint()
    }

    private fun finishMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val v = state.man.figure.vertices[state.selectedVertex!!]
        val action = MoveAction(realX - v.x, realY - v.y)
        applyAction(action, actionsPanel)
    }

    private fun finishPushMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val v = state.man.figure.vertices[state.selectedVertex!!]
        val action = PushVertexAction(state.selectedVertex!!, (realX - v.x).roundToInt(), (realY - v.y).roundToInt())
        applyAction(action, actionsPanel)
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
        g2d.color = Color.DARK_GRAY
        g2d.stroke = BasicStroke(2f)
        hole.vertices.forEachIndexed { i, v1 ->
            val v2 = hole.vertices[(i + 1).mod(hole.vertices.size)]
            g2d.drawLine(screenX(v1.x), screenY(v1.y), screenX(v2.x), screenY(v2.y))
        }
        g2d.color = Color.BLACK
        hole.vertices.forEachIndexed { i, v ->
            g2d.drawString(i.toString(), screenX(v.x).toFloat(), screenY(v.y).toFloat())
        }

        // Draw man
        g2d.color = if (ScoringUtils.fitsWithinHole(man.figure, hole)) Color.GREEN else Color.RED
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

    private val MARGIN = 30
}