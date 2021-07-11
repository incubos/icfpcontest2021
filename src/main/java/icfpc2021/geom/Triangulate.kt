package icfpc2021.geom

import icfpc2021.*
import icfpc2021.model.Hole
import icfpc2021.model.Vertex
import java.lang.Math.abs
import java.lang.RuntimeException

data class Triangle(val a: Vertex, val b: Vertex, val c: Vertex)

data class TriangleIdx(val a: Int, val b: Int, val c: Int)

class Triangulate {
    companion object {

        private fun pointInTriangle(p: Vertex, t: Triangle): Boolean {
            if (counterClockWise(t.a, t.b, p)) return false
            if (counterClockWise(t.b, t.c, p)) return false
            return !counterClockWise(t.c, t.a, p)
        }

        private fun checkIsEar(i: Int, j: Int, k: Int, polygon: List<Vertex>): Boolean {
            val t = Triangle(polygon[i], polygon[j], polygon[k])
            if (counterClockWise(t.a, t.b, t.c)) return false
            for (m in polygon.indices) {
                if (m != i && m != j && m != k && pointInTriangle(polygon[m], t)) return false
            }
            return true
        }

        fun triangulate(polygon: List<Vertex>): List<TriangleIdx> {
            val l = IntArray(polygon.size)
            val r = IntArray(polygon.size)
            for (i in polygon.indices) {
                l[i] = (i - 1 + polygon.size) % polygon.size
                r[i] = (i + 1 + polygon.size) % polygon.size
            }
            val triangulation = arrayListOf<TriangleIdx>()
            var i = polygon.size - 1
            var attemps = 0
            while (triangulation.size < polygon.size - 2) {
                i = r[i]
                if (checkIsEar(l[i], i, r[i], polygon)) {
                    triangulation.add(TriangleIdx(l[i], i, r[i]))
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

fun triangulateHolesInHole(hole: Hole,
                           holeConvexHull: List<Vertex> = convexHull(hole.vertices)): List<TriangleIdx> {
    val result = arrayListOf<TriangleIdx>()
    for (i in holeConvexHull.indices) {
        val next = (i + 1) % holeConvexHull.size
        val chVertex = hole.vertices.indexOf(holeConvexHull[i])
        val nextChVertex = hole.vertices.indexOf(holeConvexHull[next])
        // Some vertices are missing in convex hole
        if ((abs(nextChVertex - chVertex) + hole.vertices.size) % hole.vertices.size != 1) {
            // Find direction
            val missingEdges = (nextChVertex - chVertex + hole.vertices.size) % hole.vertices.size
            val step = if (missingEdges < hole.vertices.size / 2) 1 else -1
            val holeVerticesIndexes = arrayListOf<Int>()
            var currentVertex = chVertex
            while (currentVertex != (nextChVertex + step + hole.vertices.size) % hole.vertices.size) {
                holeVerticesIndexes.add(currentVertex)
                currentVertex = (currentVertex + step + hole.vertices.size) % hole.vertices.size
            }
            val polygon = holeVerticesIndexes.map { hole.vertices[it] }.toMutableList()
            // Some vertices might lie on a single line
            while (polygon.size >= 4 && abs(triangleArea(polygon.first(), polygon[1], polygon.last())) < EPSILON) {
                polygon.removeFirst()
                holeVerticesIndexes.removeFirst()
            }
            while (polygon.size >= 4 && abs(triangleArea(polygon.first(), polygon[polygon.size - 2], polygon.last())) < EPSILON) {
                polygon.removeLast()
                holeVerticesIndexes.removeLast()
            }
            val triangles = Triangulate.triangulate(polygon)
            for (triangle in triangles) {
               result.add(TriangleIdx(
                   holeVerticesIndexes[triangle.a],
                   holeVerticesIndexes[triangle.b],
                   holeVerticesIndexes[triangle.c]))
            }
        }
    }
    return result
}