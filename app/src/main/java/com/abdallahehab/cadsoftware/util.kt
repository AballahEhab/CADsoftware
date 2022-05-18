package com.abdallahehab.cadsoftware

import androidx.fragment.app.DialogFragment
import java.text.DecimalFormat
import kotlin.math.pow


fun <E> MutableList<E>.addColumn(list: MutableList<Double>) {
    (this as MutableList<MutableList<Double>>).forEachIndexed { index, mutableList ->
        mutableList.add(list[index])
    }
}

fun <E> MutableList<E>.removeColumn(columnPointer: Int) {
    (this as MutableList<MutableList<Double>>).forEach {
        it.removeAt(columnPointer)

    }

}

fun <E> MutableList<E>.transpose(): MutableList<MutableList<Double>> {
    this as MutableList<MutableList<Double>>
    var dummyMatrix: MutableList<MutableList<Double>> = mutableListOf()
    for (i in 0 until this[0].size) {
        dummyMatrix.add((this as MutableList<MutableList<Double>>).getColumn(i+1))
    }
    return dummyMatrix
}

fun<E> MutableList<E>.getColumn(colNr: Int ): MutableList<Double> = (this as MutableList<MutableList<Double>>).map { it[colNr-1] }.toMutableList()

fun <E> MutableList<E>.detOf2x2Matrix() : Double = (this as MutableList<MutableList<Double>>)[0][0]*this[1][1] -   this[0][1]*this[1][0]

fun <E> MutableList<E>.getTheMinorOfElemnt(i:Int,j:Int): Double {
    var dummyMatrix: MutableList<MutableList<Double>> = mutableListOf()


    (this as MutableList<MutableList<Double>>).forEach{
        dummyMatrix.add(it.toMutableList())
    }
    dummyMatrix.removeColumn(j)
    dummyMatrix.removeAt(i)




    return dummyMatrix[0][0]*dummyMatrix[1][1] - dummyMatrix[0][1]*dummyMatrix[1][0]
}

fun <E> MutableList<E>.getMatrixOfMinorsFor3x3Matrix(): MutableList<MutableList<Double>>{
    val matrixOfMinors  = MutableList<MutableList<Double>>(this.size) { mutableListOf<Double>()}
    for (item in this.withIndex()){
        for (element  in (item.value as MutableList<Double>).withIndex()){
            matrixOfMinors[item.index].add(this.getTheMinorOfElemnt(item.index,element.index))
        }
    }
    return matrixOfMinors

}

fun <E> MutableList<E>.getInverseMatrixOf_2x2(): MutableList<MutableList<Double>>{
    var dummy_matrix = MutableList<MutableList<Double>>(2){ MutableList<Double>(2){0.0} }
    this as MutableList<MutableList<Double>>
    dummy_matrix[0][0] = this[1][1]
    dummy_matrix[1][0] = - this[1][0]
    dummy_matrix[0][1] = - this[0][1]
    dummy_matrix[1][1] = this[0][0]

    dummy_matrix = dummy_matrix.multiplyTheMatrixWithNum((1.0/dummy_matrix.detOf2x2Matrix()))
    return dummy_matrix
}

fun <E> MutableList<E>.getInverseMatrixOf_3x3(): MutableList<MutableList<Double>>{
    this as MutableList<MutableList<Double>>
    val matrixOfMinors  = this.getMatrixOfMinorsFor3x3Matrix()


    for (item in matrixOfMinors.withIndex()){
        for (element  in (item.value as MutableList<Double>).withIndex()){
            val x = ((element.value)
                    *((-1).toDouble()
                .pow((item.index+1)
                        +(element.index+1))))
            matrixOfMinors[item.index][element.index] = x
        } // converthed the matrix of minors to a matrix of cofactors
    }

    val determinant = 1 /(this[0].multiplyTwoSingleDimentionMatrix(matrixOfMinors[0])).toDouble()

    return matrixOfMinors.transpose().map{ mutableList ->
        mutableList.map {
            (it * determinant).round(2)
        }.toMutableList()
    } .toMutableList()// returning the Adjoint matrix
}

fun <E> MutableList<E>.multiplyWith(opList:MutableList<MutableList<Double>>) :MutableList<MutableList<Double>>  {
    (this as MutableList<MutableList<Double>>)
    var dummyMatrix: MutableList<MutableList<Double>> =
        MutableList(this.size) { MutableList<Double>(opList[0].size) { 0.0 } }
    if(this[0].size == opList.size){
        this.forEachIndexed { rowIndex, mutableList ->
            for (i in 1 .. opList[0].size) {
                dummyMatrix[rowIndex][i-1] = mutableList.multiplyTwoSingleDimentionMatrix(opList.getColumn(i).toMutableList())
            }
        }
    }

    return dummyMatrix
}

fun<E> MutableList<E>.multiplyTwoSingleDimentionMatrix(column: MutableList<Double> ):Double {
    var sum = 0.0
    this.forEachIndexed { index, i -> sum+=(i as Double *column[index]) }
    return sum
}

fun<E> MutableList<E>.multiplyTheMatrixWithNum(num: Double ):MutableList<MutableList<Double>> = (this as MutableList<MutableList<Double>>).map { mutableList -> mutableList.map { if(it!=0.0)it*num else it }.toMutableList() }.toMutableList()


fun<E> MutableList<E>.rearrangeMatrix(order: MutableList<Int> ):MutableList<MutableList<Double>> {
this as MutableList<MutableList<Double>>
var dummyMatrix = MutableList<MutableList<Double>>(this.size){ mutableListOf<Double>()}
order.forEachIndexed { index, i -> dummyMatrix[index] = this[i-1] }

    return dummyMatrix
}
fun<E> MutableList<E>.restoreArrangmentOfMatrix(order: MutableList<Int> ):MutableList<MutableList<Double>> {
    this as MutableList<MutableList<Double>>
    var dummyMatrix = MutableList<MutableList<Double>>(this.size){ mutableListOf<Double>()}
    order.forEachIndexed { index, i -> dummyMatrix[i-1] = this[index] }

    return dummyMatrix
}
fun Double.round( n:Int) : Double {
    val df = DecimalFormat("0."+String(CharArray(n) { '0' }))
    return df.format(this).toDouble()
}


 fun String.convertStringMatrixToNumricMatrix(): MutableList<MutableList<Double>> = this.lines()
    .map { rowString ->
        rowString.trim()
            .split("\\s+".toRegex())
            .map {
                it.toDouble()
            }.toMutableList()
    }.toMutableList()

fun<E> MutableList<E>.inflateMtrix(): String {
    this as MutableList<MutableList<Double>>
    var inflatedMatrix:String = "\t\t\t"
    this.forEach {
        it.forEach { d: Double ->
        inflatedMatrix += (d.toString() +"\t")

        }
        inflatedMatrix += "\n\t\t\t"
    }
    return inflatedMatrix
}