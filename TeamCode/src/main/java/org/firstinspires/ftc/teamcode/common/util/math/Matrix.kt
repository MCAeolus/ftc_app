package org.firstinspires.ftc.teamcode.common.util.math

@Suppress("UNCHECKED_CAST")
class Matrix(val h : Int, val w : Int) {


    class MatrixException(s : String) : Exception(s)

    private val matrixArray = newEmptyMatrix(h, w) //2d array to represent the matrix.


    constructor(h: Int, w: Int, matrixArray: Array<Array<Double>>) : this(h, w) {
        for (i in 0 until h) {
            for (j in 0 until w) {
                this.matrixArray[i][j] = //whole array is trimmed based off of first row. Will add default 0.0 if going over inputted array size.
                        try {
                            matrixArray[i][j]
                        } catch (e: IndexOutOfBoundsException) {
                            0.0
                        }
            }
        }
    }

    constructor(matrixArray: Array<Array<Double>>) : this(matrixArray.size, matrixArray[0].size, matrixArray)


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


    fun get(h: Int, w: Int): Double = matrixArray[h][w]

    fun set(h: Int, w: Int, v: Double) {
        matrixArray[h][w] = v
    }

    fun add(matrix: Matrix): Matrix {
        if (matrix.h != h || matrix.w != w) throw MatrixException("(adding) matrices are not same size!")

        val newMatrix = newEmptyMatrix(h, w)

        for (i in 0 until h)
            for (j in 0 until w)
                newMatrix[i][j] = matrix.get(i, j) + get(i, j)

        return Matrix(newMatrix)
    }

    fun column(position: Int): Array<Double> {
        if (position >= w || position < 0) throw MatrixException("(column) passed value larger than column index")

        val column = Array(h) { _ -> 0.0 }

        for (i in 0 until h) column[i] = get(i, position)

        return column
    }

    fun row(position: Int): Array<Double> {
        if (position >= h || position < 0) throw MatrixException("(row) passed value larger than row index")

        return matrixArray[position]
    }

    fun multiply(scalar: Double): Matrix {
        val newMatrix = newEmptyMatrix(h, w)

        for (i in 0 until h)
            for (j in 0 until w)
                newMatrix[i][j] = get(i, j) * scalar

        return Matrix(newMatrix)
    }

    fun transpose(): Matrix {
        val newMatrix = newEmptyMatrix(w, h)

        for (i in 0 until h)
            for (j in 0 until w)
                newMatrix[j][i] = get(i, j)

        return Matrix(newMatrix)
    }

    fun multiply(matrix: Matrix): Matrix { //we are the left, the passed matrix is the right matrix
        if (w != matrix.h) throw MatrixException("(multiplication) matrices don't match")

        val newMatrix = newEmptyMatrix(h, matrix.w)

        for( i in 0 until h)
            for(j in 0 until w) newMatrix[i][j] = dot(row(i), matrix.column(j))

        return Matrix(newMatrix)
    }

    fun addRow(from : Int, to : Int) : Matrix {
        val newMatrix = newCopiedMatrix()

        val addedRow = addTogether(row(from), row(to))

        newMatrix[to] = addedRow

        return Matrix(newMatrix)
    }

    fun multiplyRow(constant : Double, to : Int) : Matrix {
        val newMatrix = newCopiedMatrix()

        val multipliedRow = Array(w){_ -> 0.0}

        for( i in 0 until w) multipliedRow[i] = get(to, i) * constant

        return Matrix(newMatrix)
    }

    fun switchRows(row1 : Int, row2 : Int) : Matrix {
        val newMatrix = newCopiedMatrix()

        val temprow = row(row1)
        matrixArray[row1] = row(row2)
        matrixArray[row2] = temprow

        return Matrix(newMatrix)
    }

    //utils
    private fun newEmptyMatrix(h: Int, w: Int) = Array(h) { _ -> Array(w) { _ -> 0.0 } }

    private fun newCopiedMatrix() = matrixArray.clone()

    private fun addTogether(array1 : Array<Double>, array2 : Array<Double>) : Array<Double> {
        if(array1.size != array2.size) throw MatrixException("unequal array sizes in array addition")

        val returnArray = Array(array1.size){ _ -> 0.0}
        for(i in 0 until array1.size)  returnArray[i] = array1[i] + array2[i]

        return returnArray
    }

    private fun dot(array1: Array<Double>, array2: Array<Double>): Double {
        if (array1.size != array2.size) throw MatrixException("unequal array sizes in a dot product.")

        var dot = 0.0

        for (i in 0 until array1.size) dot += array1[i] * array2[i]

        return dot
    }

}