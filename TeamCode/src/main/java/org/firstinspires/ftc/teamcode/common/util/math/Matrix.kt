package org.firstinspires.ftc.teamcode.common.util.math

@Suppress("UNCHECKED_CAST")
class Matrix(val h : Int, val w : Int) {


    class UnequalMatrixSizesException : Exception("The two matrices are not equal!")

    private val matrixArray = newEmptyMatrix(h, w) //2d array to represent the matrix.


    constructor(h : Int, w : Int, matrixArray : Array<Array<Double>>) : this(h, w) {
        for(i in 0 until h) {
            for(j in 0 until w) {
                this.matrixArray[i][j] = //whole array is trimmed based off of first row. Will add default 0.0 if going over inputted array size.
                        try {
                            matrixArray[i][j]
                        } catch ( e : IndexOutOfBoundsException) {
                            0.0
                        }
            }
        }
    }

    constructor(matrixArray : Array<Array<Double>>) : this(matrixArray.size, matrixArray[0].size, matrixArray)


    //based off of Wikipedia matrix article.
    //https://en.wikipedia.org/wiki/Matrix_(mathematics)
    //(and my basic knowledge of matrices from pre-calculus)

    //Matrix format (not mathematics based)
    //form a(h, w)
    //where a is an entry, h is the height, and w is the width (from top-left to bottom-right)
    // _ _ _ _ _ _ _ _
    //|a(0,0)...a(0,w)|
    //|a(1,0)...a(1,w)|
    //|.              |
    //|. .            |
    //|.   .          |   = [Matrix]
    //|.     .        |
    //|.       .      |
    //|a(h,0)...a(h,w)|
    // ¯ ¯ ¯ ¯ ¯ ¯ ¯ ¯


    fun get(h : Int, w : Int) : Double = matrixArray[h][w]

    fun set(h : Int, w : Int, v : Double) {
        matrixArray[h][w] = v
    }

    fun add(matrix : Matrix) : Matrix {
        if(matrix.h != h || matrix.w != w) throw UnequalMatrixSizesException()

        val newMatrix = newEmptyMatrix(h, w)

        for(i in 0 until h)
            for(j in 0 until w)
                newMatrix[i][j] = matrix.get(i, j) + get(i, j)

        return Matrix(newMatrix)
    }

    fun multiply(scalar : Double) : Matrix {
        val newMatrix = newEmptyMatrix(h, w)

        for(i in 0 until h)
            for(j in 0 until w)
                newMatrix[i][j] = get(i, j) * scalar

        return Matrix(newMatrix)
    }

    fun transpose() : Matrix {
        val newMatrix = newEmptyMatrix(w, h)

        for(i in 0 until h)
            for(j in 0 until w)
                newMatrix[j][i] = get(i, j)

        return Matrix(newMatrix)
    }

    fun multiply(matrix : Matrix) : Matrix { //we are the left, the passed matrix is the right matrix
        if(h != matrix.w) throw UnequalMatrixSizesException()

        val newMatrix = newEmptyMatrix(h, matrix.w)

        for(n in 0 until h) {

        }


        //TODO matrix multiplication


        return Matrix(newMatrix)
    }


    private fun newEmptyMatrix(h : Int, w : Int) = Array(h){ _ -> Array(w){ _ -> 0 as Double}}

}