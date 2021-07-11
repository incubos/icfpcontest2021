package icfpc2021

import icfpc2021.model.Vertex
import kotlin.math.abs

/**
 * Adapted form https://rosettacode.org/wiki/Convex_hull#Kotlin
 */
fun convexHull(pO: List<Vertex>): List<Vertex> {
    val p = pO.sorted()
    val h = mutableListOf<Vertex>()

    // lower hull
    for (pt in p) {
        while (h.size >= 2 && !counterClockWise(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    // upper hull
    val t = h.size + 1
    for (i in p.size - 2 downTo 0) {
        val pt = p[i]
        while (h.size >= t && !counterClockWise(h[h.size - 2], h.last(), pt)) {
            h.removeAt(h.lastIndex)
        }
        h.add(pt)
    }

    h.removeAt(h.lastIndex)
    return h
}

const val EPSILON = 1e-6

/* ccw returns true if the three points make a counter-clockwise turn */
fun counterClockWise(a: Vertex, b: Vertex, c: Vertex) = triangleArea(a, b, c) < -EPSILON

fun triangleArea(a: Vertex, b: Vertex, c: Vertex) =
    (a.x * b.y - a.y * b.x + a.y * c.x - a.x * c.y + b.x * c.y - c.x * b.y) / 2.0

fun area(convexHull: List<Vertex>): Double {
    return abs(0.5 * (convexHull.indices.sumOf { convexHull[it].x * convexHull[(it + 1).mod(convexHull.size)].y } -
            convexHull.indices.sumOf { convexHull[it].y * convexHull[(it + 1).mod(convexHull.size)].x }))
}