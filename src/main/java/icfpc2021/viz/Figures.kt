package icfpc2021.viz

data class Hole(val points: List<Point>)

data class Point(val x: Float, val y: Float) {
    constructor(xI: Int, yI: Int) : this(xI.toFloat(), yI.toFloat())
}


data class Figure(val vertices: List<Point>, val edges: List<Pair<Int, Int>>) {
    init {
        edges.forEach { (p1, p2) ->
            require(p1 in vertices.indices) { "Illegal index $p1" }
            require(p2 in vertices.indices) { "Illegal index $p2" }
        }
    }
}

data class LambdaMan(val figure: Figure, val epsilon: Float)