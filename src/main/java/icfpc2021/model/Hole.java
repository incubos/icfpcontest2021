package icfpc2021.model;

import icfpc2021.geom.Triangle;
import icfpc2021.geom.Triangulate;
import icfpc2021.geom.TriangulateKt;

import java.util.List;

import static icfpc2021.ConvexHullKt.convexHull;

public class Hole {
    public List<Vertex> vertices;
    private List<Vertex> myHoleConvexHull;
    private List<Triangle> myHoleTriangulation;
    private List<Triangle> myHolesInHoleTriangulation;

    public Hole(List<Vertex> vertices) {
        this.vertices = vertices;
    }

    public List<Triangle> holeTriangulation() {
        if (myHoleTriangulation == null) {
            myHoleTriangulation = Triangulate.Companion.triangulate(vertices);
        }
        return myHoleTriangulation;
    }

    public List<Triangle> holesInHoleTriangulation() {
        if (myHolesInHoleTriangulation == null) {
            myHolesInHoleTriangulation = TriangulateKt.triangulateHolesInHole(this, holeConvexHull());
        }
        return myHolesInHoleTriangulation;
    }

    public List<Vertex> holeConvexHull() {
        if (myHoleConvexHull == null) {
            myHoleConvexHull = convexHull(vertices);
        }
        return myHoleConvexHull;
    }

}
