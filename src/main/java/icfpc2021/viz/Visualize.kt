import icfpc2021.viz.Hole
import icfpc2021.viz.LambdaMan
import icfpc2021.viz.Point
import icfpc2021.viz.Figure
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

    val frame = object: JFrame("Visualizator") {
        override fun paint(g: Graphics) {
            // Draw hole
            g.color = Color.BLACK
            (g as Graphics2D).stroke = BasicStroke(2f)
            hole.points.forEachIndexed { i, p1 ->
                val p2 = hole.points[(i + 1).mod(hole.points.size)]
                g.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
            }

            // Draw man
            g.color = Color.RED
            (g as Graphics2D).stroke = BasicStroke(3f)
            man.figure.edges.forEach { (p1I, p2I) ->
                val p1 = man.figure.vertices[p1I]
                val p2 = man.figure.vertices[p2I]
                g.drawLine(screenX(p1.x), screenY(p1.y), screenX(p2.x), screenY(p2.y))
            }
        }

        private fun screenX(x: Float) = (M + (W - M * 2) * (x - minX()) / (maxX() - minX())).toInt()
        private fun screenY(y: Float) = (M + (H - M * 2) * (y - minY()) / (maxY() - minY())).toInt()
    }

    private fun minX() = min(man.figure.vertices.minOf { it.x }, hole.points.minOf { it.x })
    private fun maxX() = max(man.figure.vertices.maxOf { it.x }, hole.points.maxOf { it.x })
    private fun minY() = min(man.figure.vertices.minOf { it.y }, hole.points.minOf { it.y })
    private fun maxY() = max(man.figure.vertices.maxOf { it.y }, hole.points.maxOf { it.y })

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
        // {"hole":[[28,0],[56,4],[0,4]],"epsilon":0,"figure":{"edges":[[0,1],[0,2],[1,3],[2,3]],"vertices":[[0,20],[20,0],[20,40],[40,20]]}}
        val hole = Hole(
            listOf(
                Point(28, 0), Point(56, 4), Point(0, 4)
            )
        )
        val man = LambdaMan(
            figure = Figure(
                vertices = listOf(
                    Point(15, 21), Point(34, 0), Point(0, 45), Point(19, 24)
                ),
                edges = listOf(0 to 1, 0 to 2, 1 to 3, 2 to 3)
            ), epsilon = 1e-2f
        )

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
