package icfpc2021.geom

import icfpc2021.counterClockWise
import icfpc2021.model.Vertex

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
            while (triangulation.size < polygon.size - 2) {
                i = r[i]
                if (checkIsEar(l[i], i, r[i], polygon)) {
                    triangulation.add(TriangleIdx(l[i], i, r[i]))
                    l[r[i]] = l[i]
                    r[l[i]] = r[i]
                }
            }
            return triangulation
        }
    }
}