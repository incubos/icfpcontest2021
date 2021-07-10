package icfpc2021.geom

import icfpc2021.model.Vertex
import org.apache.commons.math3.linear.EigenDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealMatrix
import org.apache.commons.math3.stat.correlation.Covariance


fun principalComponents(vertices: List<Vertex>): List<Vertex> {
    val pointsArray = vertices.map { doubleArrayOf(it.x, it.y) }.toTypedArray()
    val realMatrix: RealMatrix = MatrixUtils.createRealMatrix(pointsArray)
    //create covariance matrix of points, then find eigen vectors
    //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
    //create covariance matrix of points, then find eigen vectors
    //see https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
    val covariance: Covariance = Covariance(realMatrix)
    val covarianceMatrix: RealMatrix = covariance.covarianceMatrix
    val ed = EigenDecomposition(covarianceMatrix)
    val evs = ed.realEigenvalues
    return evs.indices.map { val eigenvector = ed.getEigenvector(it)
        Vertex(eigenvector.getEntry(0), eigenvector.getEntry(1))
    }
}