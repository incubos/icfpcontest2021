package icfpc2021.viz

import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

class ActionsPanel : JPanel(BorderLayout()) {
    val status = JLabel("Ready")

    fun disableButtons() {
        moveButton.isEnabled = false
        rotateButton.isEnabled = false
    }

    fun enableButtons() {
        moveButton.isEnabled = true
        rotateButton.isEnabled = true
    }


    val moveButton = JButton("Move").apply {
        isEnabled = false
    }
    val rotateButton = JButton("Rotate").apply {
        isEnabled = false;
    }

    init {
        border = BorderFactory.createLineBorder(Color.GRAY)
        add(status, BorderLayout.LINE_START)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(moveButton)
            add(rotateButton)
        }, BorderLayout.EAST)
    }
}
