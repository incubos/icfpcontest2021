package icfpc2021

import icfpc2021.model.Vertex

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

/* ccw returns true if the three points make a counter-clockwise turn */
fun counterClockWise(a: Vertex, b: Vertex, c: Vertex) =
    ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x))