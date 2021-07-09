package icfpc2021.viz

import icfpc2021.actions.Action
import icfpc2021.model.Hole
import icfpc2021.model.LambdaMan
import kotlin.math.max
import kotlin.math.min

class State(val hole: Hole, val startMan: LambdaMan) {
    val man = startMan
    val previousMen = arrayListOf<LambdaMan>()
    val actions = arrayListOf<Action>()

    fun minX() = min(man.figure.vertices.minOf { it.x }, hole.vertices.minOf { it.x })
    fun maxX() = max(man.figure.vertices.maxOf { it.x }, hole.vertices.maxOf { it.x })
    fun minY() = min(man.figure.vertices.minOf { it.y }, hole.vertices.minOf { it.y })
    fun maxY() = max(man.figure.vertices.maxOf { it.y }, hole.vertices.maxOf { it.y })
}