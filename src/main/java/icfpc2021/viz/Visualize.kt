import icfpc2021.model.*
import java.awt.*
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
        // Figure 12 example
        // {"hole":[[28,0],[56,4],[0,4]],"epsilon":0,"figure":{"edges":[[0,1],[0,2],[1,3],[2,3]],"vertices":[[0,20],[20,0],[20,40],[40,20]]}}
        val hole = Hole().apply {
            vertices = listOf(Vertex(28.0, 0.0), Vertex(56.0, 4.0), Vertex(0.0, 4.0))
        }
        val man = LambdaMan().apply {
            figure = Figure().apply {
                vertices = listOf(
                    Vertex(15.0, 21.0), Vertex(34.0, 0.0), Vertex(0.0, 45.0), Vertex(19.0, 24.0)
                )
                edges = listOf(Edge(0, 1), Edge(0, 2), Edge(1, 3), Edge(2, 3))
            }
            epsilon = 1e-2
        }

        @JvmStatic
        fun main(args: Array<String>) {
            Visualize(hole, man).show()
            Runtime.getRuntime().addShutdownHook(object : Thread() {
                override fun run() {
                    println("Shutdown hook")
                }
            })

        }
    }
}
