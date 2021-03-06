package icfpc2021.viz

import com.fasterxml.jackson.databind.ObjectMapper
import icfpc2021.ScoringUtils
import icfpc2021.actions.*
import icfpc2021.actions.Action
import icfpc2021.model.Figure
import icfpc2021.model.Pose
import icfpc2021.model.Task
import icfpc2021.model.Vertex
import icfpc2021.strategy.AutoCenterStrategy
import icfpc2021.strategy.AutoKutuzoffStrategy
import icfpc2021.strategy.PosifyEdges
import java.awt.*
import java.awt.event.*
import java.nio.file.Path
import javax.swing.*
import kotlin.io.path.*
import kotlin.math.*


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

                if (state.actionInProcess == MoveVertexToGridAction::class.simpleName) {
                    finishMoveVertexToGridAction(actionsPanel, realX, realY)
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

        actionsPanel.moveVertexToGridButton.addActionListener {
            state.actionInProcess = MoveVertexToGridAction::class.simpleName
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

        actionsPanel.autoCenterStrategyButton.addActionListener {
            finishAutoCenterStrategy(actionsPanel)
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
        actionsPanel.loadButton.addActionListener {
            finishLoadAction(actionsPanel)
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
            state.reset()
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
        val strategy = PosifyEdges()
        state.applyStrategy(strategy)
        actionsPanel.status.text = "$state $strategy applied successfully"
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

    private fun finishLoadAction(actionsPanel: ActionsPanel) {
        val path = Path.of("solutions", state.taskName)
        if (path.exists()) {
            val fromJsonSolutionFile = Task.fromJsonSolutionFile(path)
            val solution = fromJsonSolutionFile;
            state.reset()
            state.applyAction(object: Action {
                override fun apply(state: State, figure: Figure): Figure =
                    Figure(solution, state.originalMan.figure.edges)

                override fun toString(): String = "Load"
            })
            actionsPanel.status.text = "$state Successfully loaded from ${path.name}"
            repaint()
        } else {
            actionsPanel.status.text = "$state No solution found"
        }
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

    private fun finishMoveVertexToGridAction(actionsPanel: ActionsPanel, realX: Double, realY: Double) {
        val action = Action.checked(
            MoveVertexToGridAction(
                state.selectedVertex!!,
                realX.roundToInt(),
                realY.roundToInt()
            )
        )
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

    private fun finishAutoCenterStrategy(actionsPanel: ActionsPanel) {
        val strategy = AutoCenterStrategy()
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

        // Draw hole
        g2d.color = Color.WHITE
        g2d.fillPolygon(
            hole.vertices.map { screenX(it.x) }.toIntArray(),
            hole.vertices.map { screenY(it.y) }.toIntArray(),
            hole.vertices.size
        )
        g2d.color = Color.LIGHT_GRAY
        for (triangleIdx in state.hole.holeTriangulation()) {
            val vA = hole.vertices[triangleIdx.a]
            val vB = hole.vertices[triangleIdx.b]
            val vC = hole.vertices[triangleIdx.c]
            g2d.drawLine(screenX(vA.x), screenY(vA.y), screenX(vB.x), screenY(vB.y))
            g2d.drawLine(screenX(vA.x), screenY(vA.y), screenX(vC.x), screenY(vC.y))
            g2d.drawLine(screenX(vC.x), screenY(vC.y), screenX(vB.x), screenY(vB.y))
        }
        g2d.color = Color.BLUE
        for (triangleIdx in state.hole.holesInHoleTriangulation()) {
            val vA = hole.vertices[triangleIdx.a]
            val vB = hole.vertices[triangleIdx.b]
            val vC = hole.vertices[triangleIdx.c]
            g2d.drawLine(screenX(vA.x), screenY(vA.y), screenX(vB.x), screenY(vB.y))
            g2d.drawLine(screenX(vA.x), screenY(vA.y), screenX(vC.x), screenY(vC.y))
            g2d.drawLine(screenX(vC.x), screenY(vC.y), screenX(vB.x), screenY(vB.y))
        }

        // Draw int grid
        val minX = ceil(state.minX())
        val maxX = ceil(state.maxX())
        val minY = floor(state.minY())
        val maxY = ceil(state.maxY())
        font = g2d.font
        g2d.font = font.deriveFont(7f)
        for (x in minX.toInt() until maxX.toInt() + 1) {
            g2d.color = Color(10, 10, 10, 40)
            g2d.stroke = BasicStroke(0.5f)
            g2d.drawLine(screenX(x.toDouble()), screenY(minY), screenX(x.toDouble()), screenY(maxY))
            g2d.color = Color.BLACK
            g2d.drawString(x.toString(), screenX(x.toDouble()), screenY(minY) - 5)
            g2d.drawString(x.toString(), screenX(x.toDouble()), screenY(maxY) + 15)
        }
        for (y in minY.toInt() until maxY.toInt() + 1) {
            g2d.color = Color(10, 10, 10, 40)
            g2d.stroke = BasicStroke(0.5f)
            g2d.drawLine(screenX(minX), screenY(y.toDouble()), screenX(maxX), screenY(y.toDouble()))
            g2d.color = Color.BLACK
            g2d.drawString(y.toString(), screenX(minX) - 15, screenY(y.toDouble()))
            g2d.drawString(y.toString(), screenX(maxX) + 15, screenY(y.toDouble()))
        }
        g2d.font = font


        g2d.color = Color.DARK_GRAY
        g2d.stroke = BasicStroke(3f)
        drawVertices(g2d, hole.vertices)
        g2d.color = Color.BLACK
        hole.vertices.forEachIndexed { i, v ->
            drawString(g2d, i.toString(), screenX(v.x), screenY(v.y))
        }

        // Draw man
        val manColor = if (ScoringUtils.fitsWithinHole(man.figure, hole)) Color.GREEN else Color.RED
        g2d.color = manColor
        man.figure.edges.forEachIndexed { i, e ->
            val p1 = man.figure.vertices[e.start]
            val p2 = man.figure.vertices[e.end]
            val stroke =
                if (ScoringUtils.checkEdge(man.figure, state.originalMan.figure, state.originalMan.epsilon, i)) {
                    BasicStroke(5f)
                } else {
                    BasicStroke(5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, floatArrayOf(9f), 0f)
                }
            g2d.stroke = stroke
            g2d.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
        }
        val defaultFont = g2d.font
        man.figure.vertices.forEachIndexed { i, v ->
            g2d.color = if (ScoringUtils.isIntegerCoordinates(v)) Color.GREEN else Color.RED
            if (i == state.selectedVertex) {
                g2d.fillOval(screenX(v.x) - 10, screenY(v.y) - 10, 20, 20)
            } else {
                g2d.fillOval(screenX(v.x) - 5, screenY(v.y) - 5, 10, 10)
            }
            drawString(g2d, i.toString(), screenX(v.x), screenY(v.y), Color.BLACK)
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
    private fun screenX(x: Double) = (MARGINX + (width - MARGINX * 2) * (x - state.minCs()) / (state.maxCs() - state.minCs())).toInt()
    private fun screenY(y: Double) = (MARGINY + (height - MARGINY * 2) * (y - state.minCs()) / (state.maxCs() - state.minCs())).toInt()
    private fun realX(xScreen: Int) = state.minCs() + (xScreen - MARGINX) * (state.maxCs() - state.minCs()) / (width - MARGINX * 2)
    private fun realY(yScreen: Int) = state.minCs() + (yScreen - MARGINY) * (state.maxCs() - state.minCs()) / (height - MARGINY * 2)

    private fun minWH() = min(width, height)

    private val MARGINX
        get() = 10 + (width - minWH()) / 2
    private val MARGINY
        get() = 10 + (height - minWH()) / 2

}