import icfpc2021.model.*
import java.awt.*
import java.nio.file.Path
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants
import kotlin.math.max
import kotlin.math.min

class Visualize(val hole: Hole, val man: LambdaMan) {
    val W = 800
    val H = 600
    val M = 100

    val frame = object : JFrame("Visualizator") {
        override fun paint(g: Graphics) {
            val g2d = g as Graphics2D
            g.color = Color.LIGHT_GRAY
            g2d.fillRect(0, 0, W, H)
            // Draw hole
            g.color = Color.WHITE
            g2d.fillPolygon(
                hole.vertices.map { screenX(it.x) }.toIntArray(),
                hole.vertices.map { screenY(it.y) }.toIntArray(),
                hole.vertices.size
            )
            g.color = Color.BLACK
            g2d.stroke = BasicStroke(2f)
            hole.vertices.forEachIndexed { i, v1 ->
                val v2 = hole.vertices[(i + 1).mod(hole.vertices.size)]
                g.drawLine(screenX(v1.x), screenY(v1.y), screenX(v2.x), screenY(v2.y))
            }

            // Draw man
            g.color = Color.RED
            g2d.stroke = BasicStroke(5f)
            man.figure.edges.forEach { e ->
                val p1 = man.figure.vertices[e.start]
                val p2 = man.figure.vertices[e.end]
                g.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
            }
        }

        private fun screenX(x: Double) = (M + (W - M * 2) * (x - minX()) / (maxX() - minX())).toInt()
        private fun screenY(y: Double) = (M + (H - M * 2) * (y - minY()) / (maxY() - minY())).toInt()
    }

    private fun minX() = min(man.figure.vertices.minOf { it.x }, hole.vertices.minOf { it.x })
    private fun maxX() = max(man.figure.vertices.maxOf { it.x }, hole.vertices.maxOf { it.x })
    private fun minY() = min(man.figure.vertices.minOf { it.y }, hole.vertices.minOf { it.y })
    private fun maxY() = max(man.figure.vertices.maxOf { it.y }, hole.vertices.maxOf { it.y })

    fun show() {
        frame.apply {
            contentPane = JPanel()
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            preferredSize = Dimension(W, H)
            size = Dimension(W, H)
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
