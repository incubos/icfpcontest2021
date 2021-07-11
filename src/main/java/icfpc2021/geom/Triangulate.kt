package icfpc2021.geom

import icfpc2021.*
import icfpc2021.model.Hole
import icfpc2021.model.Vertex
import java.lang.Math.abs
import java.lang.RuntimeException

data class Triangle(val a: Int, val b: Int, val c: Int)

class Triangulate {
    companion object {

        private fun pointInTriangle(p: Vertex, a: Vertex, b: Vertex, c: Vertex): Boolean {
            if (counterClockWise(a, b, p)) return false
            if (counterClockWise(b, c, p)) return false
            return !counterClockWise(c, a, p)
        }

        private fun checkIsEar(i: Int, j: Int, k: Int, polygon: List<Vertex>): Boolean {
            val a = polygon[i]
            val b = polygon[j]
            val c = polygon[k]
            if (abs(triangleArea(a, b, c)) < EPSILON) {
                return false
            }
            if (counterClockWise(a, b, c)) return false
            for (m in polygon.indices) {
                if (m != i && m != j && m != k && pointInTriangle(polygon[m], a, b, c)) return false
            }
            return true
        }

        fun triangulate(polygon: List<Vertex>): List<Triangle> {
            val l = IntArray(polygon.size)
            val r = IntArray(polygon.size)
            for (i in polygon.indices) {
                l[i] = (i - 1 + polygon.size) % polygon.size
                r[i] = (i + 1 + polygon.size) % polygon.size
            }
            val triangulation = arrayListOf<Triangle>()
            var i = polygon.size - 1
            var attemps = 0
            while (triangulation.size < polygon.size - 2) {
                i = r[i]
                if (checkIsEar(l[i], i, r[i], polygon)) {
                    triangulation.add(Triangle(l[i], i, r[i]))
                    l[r[i]] = l[i]
                    r[l[i]] = r[i]
                    attemps = 0
                } else {
                    attemps += 1
                    if (attemps > polygon.size) {
                        throw RuntimeException("Failed to process triangulate due to edge cases")
                    }
                }
            }
            return triangulation
        }
    }
}

fun triangulateHolesInHole(
    hole: Hole,
    holeConvexHull: List<Vertex> = convexHull(hole.vertices)
): List<Triangle> {
    val result = arrayListOf<Triangle>()
    for (i in holeConvexHull.indices) {
        val next = (i + 1) % holeConvexHull.size
        val chVertex = hole.vertices.indexOf(holeConvexHull[i])
        val nextChVertex = hole.vertices.indexOf(holeConvexHull[next])
        // Some vertices are missing in convex hole
        if ((abs(nextChVertex - chVertex) + hole.vertices.size) % hole.vertices.size != 1) {
            // Find direction
            val missingEdges = (nextChVertex - chVertex + hole.vertices.size) % hole.vertices.size
            val step = if (missingEdges < hole.vertices.size / 2) 1 else -1
            var holeVerticesIdx: MutableList<Int> = arrayListOf<Int>()
            var currentVertex = chVertex
            while (currentVertex != (nextChVertex + step + hole.vertices.size) % hole.vertices.size) {
                holeVerticesIdx.add(currentVertex)
                currentVertex = (currentVertex + step + hole.vertices.size) % hole.vertices.size
            }
            var polygon = holeVerticesIdx.map { hole.vertices[it] }.toMutableList()
            // Some vertices might lie on a single line
            while (polygon.size >= 4 && abs(triangleArea(polygon.first(), polygon[1], polygon.last())) < EPSILON) {
                polygon.removeFirst()
                holeVerticesIdx.removeFirst()
            }
            while (polygon.size >= 4 && abs(
                    triangleArea(
                        polygon.first(),
                        polygon[polygon.size - 2],
                        polygon.last()
                    )
                ) < EPSILON
            ) {
                polygon.removeLast()
                holeVerticesIdx.removeLast()
            }
            while (polygon.size >= 4) {
                var onLine = false
                for (k in 1..polygon.size - 2) {
                    // Single line
                    if (!onLine) {
                        if (abs(triangleArea(polygon.first(), polygon[k], polygon.last())) < EPSILON) {
                            for (t in Triangulate.triangulate(polygon.subList(0, k + 1))) {
                                result.add(Triangle(holeVerticesIdx[t.a], holeVerticesIdx[t.b], holeVerticesIdx[t.c]))
                            }
                            polygon = polygon.subList(k, polygon.size)
                            holeVerticesIdx = holeVerticesIdx.subList(k, holeVerticesIdx.size).toMutableList()
                            onLine = true
                        }
                    }
                }
                if (!onLine) {
                    break
                }
            }
            if (polygon.size >= 3) {
                for (t in Triangulate.triangulate(polygon)) {
                    result.add(Triangle(holeVerticesIdx[t.a], holeVerticesIdx[t.b], holeVerticesIdx[t.c]))
                }
            }
        }
    }
    return result
}