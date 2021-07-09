package icfpc2021.viz

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

class ActionsPanel : JPanel(BorderLayout()) {
    val status = JLabel("Ready")

    fun disableButtons() {
        moveButton.isEnabled = false
        rotateButton.isEnabled = false
        pushVertexButton.isEnabled = false
        foldSubFigureButton.isEnabled = false
    }

    fun enableButtons() {
        moveButton.isEnabled = true
        rotateButton.isEnabled = true
        pushVertexButton.isEnabled = true
        foldSubFigureButton.isEnabled = true
    }


    val moveButton = JButton("Move").apply {
        isEnabled = false
    }
    val rotateButton = JButton("Rotate").apply {
        isEnabled = false;
    }
    val pushVertexButton = JButton("PushVertex").apply {
        isEnabled = false;
    }
    val foldSubFigureButton = JButton("FoldSubfigure").apply {
        isEnabled = false;
    }

    val rollBackLastAction = JButton("Rollback last")
    val forwardButton = JButton(">")
    val backButton = JButton("<")

    init {
        add(status, BorderLayout.LINE_START)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(foldSubFigureButton)
            add(moveButton)
            add(rotateButton)
            add(pushVertexButton)
            add(rollBackLastAction)
            add(backButton)
            add(forwardButton)
        }, BorderLayout.EAST)
    }
}
