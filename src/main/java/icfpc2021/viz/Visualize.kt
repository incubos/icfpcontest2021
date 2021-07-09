import icfpc2021.model.*
import icfpc2021.viz.Field
import icfpc2021.viz.State
import java.awt.*
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

class Visualize(hole: Hole, man: LambdaMan) {
    private val frame : JFrame by lazy { JFrame("Visualizator") }
    private val state = State(hole, man)
    lateinit var field: Field

    fun show() {
        field = Field(state)
        frame.apply {
            contentPane = JPanel().apply {
                layout = BorderLayout()
                add(field, BorderLayout.CENTER)
            }
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension(800, 600)
            size = Dimension(800, 600)
            isVisible = true
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (args.size != 1) {
                error("Usage: Visualize <path-to-problem-json>")
            }
            val task = Task.fromJsonFile(Path.of(args[0]))
            val man = LambdaMan().apply {
                figure = task.figure
                epsilon = task.epsilon.toDouble()
            }
            Visualize(task.hole, man).show()
            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    println("Shutdown hook")
                }
            })

        }
    }
}
