package icfpc2021.viz

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Insets
import javax.swing.BoxLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel

class ActionsPanel : JPanel(BorderLayout()) {
    val status = JLabel("Ready")

    fun disableActions() {
        moveButton.isEnabled = false
        moveVertexToGridButton.isEnabled = false
        rotateButton.isEnabled = false
        pushVertexButton.isEnabled = false
        foldSubFigureButton.isEnabled = false
    }

    fun enableActions() {
        moveButton.isEnabled = true
        moveVertexToGridButton.isEnabled = true
        rotateButton.isEnabled = true
        pushVertexButton.isEnabled = true
        foldSubFigureButton.isEnabled = true
    }


    val moveButton = createSmallButton("Move").apply {
        isEnabled = false
    }
    val moveVertexToGridButton = createSmallButton("MoveVertexToGrid").apply {
        isEnabled = false
    }

    val rotateButton = createSmallButton("Rotate").apply {
        isEnabled = false;
    }
    val pushVertexButton = createSmallButton("Push").apply {
        isEnabled = false;
    }
    val foldSubFigureButton = createSmallButton("Fold").apply {
        isEnabled = false;
    }
    val autoKutuzoffButton = createSmallButton("Strategy AutoKutuzoff")
    val autoCenterStrategyButton = createSmallButton("Strategy Autocenter")
    val autoCenterButton = createSmallButton("AutoCenter")
    val autoRotateButton = createSmallButton("AutoRotate")
    val autoFoldButton = createSmallButton("AutoFold")

    val printButton = createSmallButton("Print")
    val loadButton = createSmallButton("Load")
    val posifyButton = createSmallButton("PosifyEdges")
    val rollBackLastAction = createSmallButton("RollbackLast")
    val forwardButton = createSmallButton(">")
    val backButton = createSmallButton("<")
    val restartButton = createSmallButton("Reset")

    init {
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(autoKutuzoffButton)
            add(autoCenterStrategyButton)
            add(autoCenterButton)
            add(autoRotateButton)
            add(autoFoldButton)
            add(foldSubFigureButton)
            add(moveButton)
            add(moveVertexToGridButton)
            add(rotateButton)
            add(pushVertexButton)
            add(rollBackLastAction)
            add(backButton)
            add(forwardButton)
            add(printButton)
            add(loadButton)
            add(posifyButton)
            add(restartButton)
        }, BorderLayout.CENTER)
        add(status, BorderLayout.PAGE_END)
    }
}

fun createSmallButton(text: String) = JButton(text).apply {
    val fontMetrics = getFontMetrics(font)
    val dimension = Dimension(fontMetrics.stringWidth(text) + 16, fontMetrics.height + 4)
    size = dimension
    margin = Insets(2, 2, 2, 2)
    preferredSize = dimension
    minimumSize = dimension
    maximumSize = dimension
}