package icfpc2021.viz

import com.fasterxml.jackson.databind.ObjectMapper
import icfpc2021.ScoringUtils
import icfpc2021.actions.*
import icfpc2021.actions.Action
import icfpc2021.convexHull
import icfpc2021.model.Pose
import icfpc2021.model.Vertex
import icfpc2021.strategy.AutoKutuzoffStrategy
import java.awt.*
import java.awt.event.*
import java.nio.file.Path
import javax.swing.*
import kotlin.io.path.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class Field(val state: State) : JPanel() {
    val objectMapper = ObjectMapper()

    fun addActionsListener(actionsPanel: ActionsPanel) {
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                val realX = realX(e.x)
                val realY = realY(e.y)
                updatePosition(actionsPanel, e, "Mouse click")

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
                        actionsPanel.disableActions()
                        state.selectedVertex = null
                        updatePosition(actionsPanel, e, "No vertex selected")
                    }
                    state.selectedVertex -> {
                        actionsPanel.disableActions()
                        state.selectedVertex = null
                        updatePosition(actionsPanel, e, "Vertex man $manVertex deselected")
                    }
                    else -> {
                        actionsPanel.enableActions()
                        state.selectedVertex = manVertex
                        updatePosition(actionsPanel, e, "Vertex man $manVertex selected")
                    }
                }
                repaint()
            }
        })

        addMouseMotionListener(object : MouseMotionAdapter() {
            override fun mouseMoved(e: MouseEvent) {
                updatePosition(actionsPanel, e)
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
            actionsPanel.disableActions()
        }


        actionsPanel.rotateButton.addActionListener {
            finishRotateAction(actionsPanel)
        }

        actionsPanel.pushVertexButton.addActionListener {
            state.actionInProcess = PushVertexAction::class.simpleName
            actionsPanel.status.text = "$state Select point to push"
            actionsPanel.disableActions()
        }

        actionsPanel.foldSubFigureButton.addActionListener {
            finishFoldAction(actionsPanel)
        }

        actionsPanel.autoKutuzoffButton.addActionListener {
            finishAutoKutuzoffStrategy(actionsPanel)
        }

        actionsPanel.autoCenterButton.addActionListener {
            finishAutoCenterAction(actionsPanel)
        }
        actionsPanel.autoRotateButton.addActionListener {
            finishAutoRotateAction(actionsPanel)
        }
        actionsPanel.autoFoldButton.addActionListener {
            finishAutoFoldAction(actionsPanel)
        }


        actionsPanel.printButton.addActionListener {
            finishPrintAction(actionsPanel)
        }
        actionsPanel.posifyButton.addActionListener {
            finishPosifyAction(actionsPanel)
        }

        actionsPanel.rollBackLastAction.addActionListener {
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableActions()
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
            actionsPanel.disableActions()
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
            actionsPanel.disableActions()
            if (state.current >= state.figures.size - 1) {
                actionsPanel.status.text = "$state Nothing to apply"
            } else {
                state.current += 1
                actionsPanel.status.text = "$state"
                repaint()
            }
        }

        actionsPanel.restartButton.addActionListener {
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableActions()
            state.actions.clear()
            state.figures.removeIf { it != state.figures.first() }
            state.current = 0
            actionsPanel.status.text = "$state Restarted"
            repaint()
        }

    }

    fun keyPressed(e: KeyEvent, actionsPanel: ActionsPanel) {
        if (e.keyCode == 27) { // Escape
            state.selectedVertex = null
            state.actionInProcess = null
            actionsPanel.disableActions()
            actionsPanel.status.text = "$state${state.printMan()}"
            repaint()
        }
    }

    private fun finishRotateAction(actionsPanel: ActionsPanel) {
        actionsPanel.status.text = "$state Enter degrees"
        state.actionInProcess = RotateAction::class.simpleName
        actionsPanel.disableActions()

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
            val action = Action.checked(RotateAction(v.x, v.y, degrees.toDouble()))
            state.applyAction(action)
            actionsPanel.status.text = "$state Rotated to $degrees"
        }
        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.disableActions()
        repaint()
    }

    private fun finishPosifyAction(actionsPanel: ActionsPanel) {
        val action = Action.checked(PosifyAction())
        state.applyAction(action)
        repaint()
    }
    private fun finishPrintAction(actionsPanel: ActionsPanel) {
        val pose = Pose.fromVertices(state.figures[state.current].vertices)
        val json = objectMapper.writeValueAsString(pose)
        val path = Path.of(state.problemPath.parent.parent.absolutePathString(), "solutions", state.taskName)
        path.deleteIfExists()
        path.createFile().writeText(json)
        actionsPanel.status.text = "$state Successfully printed to ${path.name}"
    }

    private fun finishFoldAction(actionsPanel: ActionsPanel) {
        actionsPanel.status.text = "$state Enter second vertex and subfigure vertex (e.g. 0,5)"
        state.actionInProcess = RotateAction::class.simpleName
        actionsPanel.disableActions()

        // Show input dialog
        val textComponent = JTextField("")
        val optPane = JOptionPane(JPanel(BorderLayout()).apply {
            add(JLabel("Enter second vertex and subfigure vertex (e.g. 0,5)"), BorderLayout.NORTH)
            add(JScrollPane(textComponent), BorderLayout.CENTER)
        }, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION)
        optPane.createDialog(this, "Enter vertices").apply {
            isVisible = true
        }
        if (optPane.value == JOptionPane.OK_OPTION) {
            val (second, subFigure) = try {
                Pair(textComponent.text.split(",")[0].toInt(), textComponent.text.split(",")[1].toInt())
            } catch (e: Exception) {
                Pair(0, 0)
            }
            val action = Action.checked(FoldAction(state.selectedVertex!!, second, subFigure))
            state.applyAction(action)
        }
        state.selectedVertex = null
        state.actionInProcess = null
        actionsPanel.disableActions()
        repaint()
    }

    private fun finishMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val v = state.man.figure.vertices[state.selectedVertex!!]
        val action = Action.checked(MoveAction(realX - v.x, realY - v.y))
        state.applyAction(action)
        actionsPanel.status.text = "$state $action applied successfully"
        repaint()
    }

    private fun finishPushMoveAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val action = Action.checked(PushVertexAction(state.selectedVertex!!, realX.roundToInt(), realY.roundToInt()))
        state.applyAction(action)
        actionsPanel.status.text = "$state $action applied successfully"
        repaint()
    }

    private fun finishAutoKutuzoffStrategy(actionsPanel: ActionsPanel) {
        val strategy = AutoKutuzoffStrategy()
        state.applyStrategy(strategy)
        actionsPanel.status.text = "$state $strategy applied successfully"
        repaint()
    }

    private fun finishAutoCenterAction(actionsPanel: ActionsPanel) {
        val action = Action.checked(AutoCenterAction())
        state.applyAction(action)
        actionsPanel.status.text = "$state $action applied successfully"
        repaint()
    }

    private fun finishAutoRotateAction(actionsPanel: ActionsPanel) {
        val action = Action.checked(AutoRotateAction())
        state.applyAction(action)
        actionsPanel.status.text = "$state $action applied successfully"
        repaint()
    }

    private fun finishAutoFoldAction(actionsPanel: ActionsPanel) {
        val action = Action.checked(AutoFoldAction())
        state.applyAction(action)
        actionsPanel.status.text = "$state $action applied successfully"
        repaint()
    }

    override fun paint(g: Graphics) {
        val g2d = g as Graphics2D
        val hole = state.hole
        val man = state.man
        g2d.color = Color.LIGHT_GRAY
        g2d.fillRect(0, 0, width, height)

        // Show field size
        val minCoords = "${minCs()},${minCs()}"
        drawString(g2d, minCoords, MARGIN, MARGIN + g2d.fontMetrics.height)
        val maxCoords = "${maxCs()},${maxCs()}"
        drawString(g2d, maxCoords, width - MARGIN - g2d.fontMetrics.stringWidth(maxCoords), height - MARGIN)

        // Draw hole
        g2d.color = Color.WHITE
        g2d.fillPolygon(
            hole.vertices.map { screenX(it.x) }.toIntArray(),
            hole.vertices.map { screenY(it.y) }.toIntArray(),
            hole.vertices.size
        )
        g2d.color = Color.DARK_GRAY.brighter()
        g2d.stroke = BasicStroke(1f)

        drawVertices(g2d, state.holeConvexHull)
        g2d.color = Color.DARK_GRAY
        g2d.stroke = BasicStroke(3f)
        drawVertices(g2d, hole.vertices)
        g2d.color = Color.BLACK
        hole.vertices.forEachIndexed { i, v ->
            drawString(g2d, i.toString(), screenX(v.x), screenY(v.y))
        }

        // Draw man
        val manColor = if (ScoringUtils.fitsWithinHole(man.figure, hole)) Color.GREEN else Color.RED
        g2d.color = manColor.brighter()
        g2d.stroke = BasicStroke(1f)
        drawVertices(g2d, convexHull(man.figure.vertices))
        g2d.color = manColor
        g2d.stroke = BasicStroke(5f)
        man.figure.edges.forEach { e ->
            val p1 = man.figure.vertices[e.start]
            val p2 = man.figure.vertices[e.end]
            g2d.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
        }
        val defaultFont = g2d.font
        man.figure.vertices.forEachIndexed { i, v ->
            val color = if (i == state.selectedVertex) Color.BLUE else Color.BLACK
            g2d.font = if (i == state.selectedVertex) defaultFont.deriveFont(30f) else defaultFont
            drawString(g2d, i.toString(), screenX(v.x), screenY(v.y), color)
        }
        g2d.font = defaultFont
    }

    private fun drawVertices(g2d: Graphics2D, vertices: List<Vertex>) {
        vertices.forEachIndexed { i, v1 ->
            val v2 = vertices[(i + 1).mod(vertices.size)]
            g2d.drawLine(screenX(v1.x), screenY(v1.y), screenX(v2.x), screenY(v2.y))
        }
    }

    private fun drawString(g: Graphics, string: String, x: Int, y: Int, color: Color = Color.BLACK) {
        val bounds = g.fontMetrics.getStringBounds(string, g)
        val width = bounds.height.toInt()
        val height = bounds.width.toInt()
        g.color = Color(255, 255, 255, (0.9 * 255).toInt())
        g.fillRect(x - 1, y - width - 1, height + 2, width + 2)
        g.color = color
        g.drawString(string, x, y - 1)
    }

    private fun updatePosition(actionsPanel: ActionsPanel, e: MouseEvent, msg: String = "") {
        val realX = realX(e.x)
        val realY = realY(e.y)
        val manVertexIdx = state.findVertex(state.man.figure.vertices, realX, realY)
        val manVertex = if (manVertexIdx != null) state.man.figure.vertices[manVertexIdx] else null
        val holeVertexIdx = state.findVertex(state.hole.vertices, realX, realY)
        val holeVertex = if (holeVertexIdx != null) state.hole.vertices[holeVertexIdx] else null
        // Update cursor
        val source = e.source as Component
        if (manVertexIdx != null || holeVertexIdx != null) {
            source.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        } else {
            source.cursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)
        }
        val position = "[x${realX.toInt()},y${realY.toInt()}]" +
                "[Man:${if (manVertex != null) "#$manVertexIdx(${manVertex.x.toInt()},${manVertex.y.toInt()})" else "-"}]" +
                "[Hole:${if (holeVertex != null) "#$holeVertexIdx(${holeVertex.x.toInt()},${holeVertex.y.toInt()})" else "-"}]"

        actionsPanel.status.text = "$state$position $msg"
    }

    // Screen and model coordinates conversion
    private fun screenX(x: Double) = (MARGINX + (width - MARGINX * 2) * (x - minCs()) / (maxCs() - minCs())).toInt()
    private fun screenY(y: Double) = (MARGINY + (height - MARGINY * 2) * (y - minCs()) / (maxCs() - minCs())).toInt()
    private fun realX(xScreen: Int) = minCs() + (xScreen - MARGINX) * (maxCs() - minCs()) / (width - MARGINX * 2)
    private fun realY(yScreen: Int) = minCs() + (yScreen - MARGINY) * (maxCs() - minCs()) / (height - MARGINY * 2)

    private fun minWH() = min(width, height)

    private val MARGINX = 10 + (width - minWH()) / 2
    private val MARGINY = 10 + (height - minWH()) / 2

    // Use these values to keep X / Y ratio constant
    private val MARGIN = 20
    fun minCs() = min(state.minX(), state.minY()) - MARGIN
    fun maxCs() = max(state.maxX(), state.maxY()) + MARGIN

}