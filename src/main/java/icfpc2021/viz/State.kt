package icfpc2021.viz

import icfpc2021.actions.Action
import icfpc2021.area
import icfpc2021.convexHull
import icfpc2021.model.*
import icfpc2021.strategy.Strategy
import java.nio.file.Path
import java.util.HashMap
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class State(val hole: Hole, val originalMan: LambdaMan, val taskName: String, val problemPath: Path) {
    val figures = arrayListOf<Figure>(originalMan.figure)
    val actions = arrayListOf<Action>()
    val adjacencyList = run {
        val map = HashMap<Int, MutableList<Int>>()
        originalMan.figure.edges.forEach { elem ->
            val start = map.getOrDefault(elem.start, arrayListOf())
            start.add(elem.end)
            map[elem.start] = start
            val end = map.getOrDefault(elem.end, arrayListOf())
            end.add(elem.start)
            map[elem.end] = end
        }
        map
    }

    // Used for scroll
    var current: Int = 0

    val man: LambdaMan
        get() = LambdaMan().apply {
            figure = figures[current]
            epsilon = originalMan.epsilon
        }

    val holeConvexHull = convexHull(hole.vertices)

    var selectedVertex: Int? = null
    var actionInProcess: String? = null // TODO fix me!

    fun reset() {
        actions.clear()
        figures.clear()
        figures.add(originalMan.figure)
        current = 0
    }

    fun applyAction(action: Action) {
        val newFigure = action.apply(this, man.figure)
        actions.add(action)
        figures.add(newFigure)
        current = figures.size - 1
        selectedVertex = null
        actionInProcess = null
    }

    fun applyStrategy(strategy: Strategy) {
        val actions = strategy.apply(this, man.figure)
        actions.forEach { action ->
            applyAction(action)
        }
    }

    fun minX() = min(man.figure.vertices.minOf { it.x }, hole.vertices.minOf { it.x })
    fun maxX() = max(man.figure.vertices.maxOf { it.x }, hole.vertices.maxOf { it.x })
    fun minY() = min(man.figure.vertices.minOf { it.y }, hole.vertices.minOf { it.y })
    fun maxY() = max(man.figure.vertices.maxOf { it.y }, hole.vertices.maxOf { it.y })

    fun findVertex(vertices: List<Vertex>, realX: Double, realY: Double, precision: Double = 2.0): Int? {
        return vertices.indices.map {
            val x = vertices[it].x
            val y = vertices[it].y
            it to sqrt((realX - x) * (realX - x) + (realY - y) * (realY - y))
        }.filter { it.second < precision }.minByOrNull { it.second }?.first
    }

    fun printMan() = man.figure.vertices.joinToString(",") { "(${it.x.toInt()}, ${it.y.toInt()})" }

    override fun toString(): String {
        return "[#${current + 1}/${figures.size}]" +
                "[LA ${if (current >= 1) actions[current - 1].toString() else "-"}]" +
                "[Process ${actionInProcess}][Selection ${selectedVertex}]"
    }
}