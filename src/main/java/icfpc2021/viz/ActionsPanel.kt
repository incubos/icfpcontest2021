package icfpc2021.viz

import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.Insets
import javax.swing.*

class ActionsPanel : JPanel(BorderLayout()) {
    val status = JLabel("Ready")

    fun disableActions() {
        moveButton.isEnabled = false
        rotateButton.isEnabled = false
        pushVertexButton.isEnabled = false
        foldSubFigureButton.isEnabled = false
    }

    fun enableActions() {
        moveButton.isEnabled = true
        rotateButton.isEnabled = true
        pushVertexButton.isEnabled = true
        foldSubFigureButton.isEnabled = true
    }


    val moveButton = createSmallButton("Move").apply {
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
    val autoCenterButton = createSmallButton("AutoCenter")
    val autorotateButton = createSmallButton("AutoRotate")

    val printButton = createSmallButton("Print").apply {
        isEnabled = true;
    }

    val rollBackLastAction = createSmallButton("RollbackLast")
    val forwardButton = createSmallButton(">")
    val backButton = createSmallButton("<")

    init {
        add(status, BorderLayout.LINE_START)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(autoCenterButton)
            add(autorotateButton)
            add(foldSubFigureButton)
            add(moveButton)
            add(rotateButton)
            add(pushVertexButton)
            add(rollBackLastAction)
            add(backButton)
            add(forwardButton)
            add(printButton)
        }, BorderLayout.EAST)
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