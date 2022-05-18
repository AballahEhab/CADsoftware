package com.abdallahehab.cadsoftware

import android.util.Log
import android.view.animation.BounceInterpolator

class IncidenceMatrix(private val incidenceMatrix: MutableList<MutableList<Double>>) {



    var incindenceMatrixMutable: MutableList<MutableList<Double>> = mutableListOf()

    var reducedIncidenceMatrixMutable: MutableList<MutableList<Double>> = mutableListOf()

    var incidenceLinkMatrix: MutableList<MutableList<Double>> = mutableListOf()

    private var incidenceTreeMatrix :MutableList<MutableList<Double>> = mutableListOf()

    var tieSetMatrix :MutableList<MutableList<Double>> = mutableListOf()

    var numOfNodes = incidenceMatrix.size

    val numOfBranches = incidenceMatrix[0].size

    var orderOfLinksAndBranchedToIncidenceMatrix: MutableList<Int> =
        (1..numOfBranches).toCollection(mutableListOf<Int>())

    init {

        incindenceMatrixMutable= incidenceMatrix
        completeIncidenceMatrix()

        incidenceTreeMatrix = rearrangeIncidenceMatrixToGetIncidenceTreeMatrix()

        getIncidenceLinkMatrix()

        findTieSetMatrix()
    }



    private fun column(
        colNr: Int,
        matrixP: MutableList<MutableList<Double>> = incidenceMatrix as MutableList<MutableList<Double>>
    ): List<Double> = matrixP.map { it[colNr - 1] }


    // step one completing the incidence matrix to ensure the num of nodes
    private fun completeIncidenceMatrix() {
        if (!column(1).containsAll(listOf<Double>(1.0, -1.0))) {
            addTheMissingRowToIncidenceMatrix()
        }
        reducedIncidenceMatrixMutable = incindenceMatrixMutable.dropLast(1).toMutableList()
    }

    private fun addTheMissingRowToIncidenceMatrix() {
        val missingRowMutableList = mutableListOf<Double>()
        for (i in 0 until numOfBranches) {
            val columnI = column(i + 1)
            val hasStartNode: Boolean = columnI.contains(1.0)
            val hasEndnode: Boolean = columnI.contains(-1.0)
            if (hasStartNode) {
                if (hasEndnode) {
                    missingRowMutableList.add(0.0)
                } else {
                    missingRowMutableList.add(-1.0)
                }
            } else {
                missingRowMutableList.add(1.0)
            }
        }
        incindenceMatrixMutable.add(missingRowMutableList)
        numOfNodes++



    }


    // step two getting incidence tree matrix out of incidence matrix
    fun rearrangeIncidenceMatrixToGetIncidenceTreeMatrix(
        incidenceMatrixP: MutableList<MutableList<Double>> = reducedIncidenceMatrixMutable,
        incidenceTreeMatrixP: MutableList<MutableList<Double>>? = null
    ): MutableList<MutableList<Double>> {


        var dummyIncidenceMatrix = mutableListOf<MutableList<Double>>()



        incidenceMatrixP.forEach {
            dummyIncidenceMatrix.add(it.toMutableList())
        }

        var incidenceTreeMatrix: MutableList<MutableList<Double>> = mutableListOf()

        // only usable for the first time and over performance for the rest of times
        incidenceTreeMatrix =
            incidenceTreeMatrixP ?: extractTreeMatrixOverIncidenceMatrix(dummyIncidenceMatrix)

        var listOfIndexesOfBranchespassingThroughNodes: MutableList<MutableList<Int>> =
            mutableListOf()

        incidenceTreeMatrix.forEach { incidenceMatrixRow ->

            checkIfMoreThanLinkPassThroughTheNode(incidenceMatrixRow)?.let {

                listOfIndexesOfBranchespassingThroughNodes.add(it)
            }
        }

        if (listOfIndexesOfBranchespassingThroughNodes.size > 1) {
            for (rowList in listOfIndexesOfBranchespassingThroughNodes.withIndex())  {
                var rowPointer = rowList.index + 1
                var columnPointer = 0
                while (rowPointer < listOfIndexesOfBranchespassingThroughNodes.size) {

                    while (columnPointer<rowList.value.size){
                        listOfIndexesOfBranchespassingThroughNodes[rowPointer].forEach loop@{
                            if (rowList.value[columnPointer] == it) {
                                dummyIncidenceMatrix.removeColumn(it)
                                val columnOrder = orderOfLinksAndBranchedToIncidenceMatrix[it]
                                orderOfLinksAndBranchedToIncidenceMatrix.removeAt(it)
                                orderOfLinksAndBranchedToIncidenceMatrix.add(columnOrder)
                                incidenceTreeMatrix =
                                    extractTreeMatrixOverIncidenceMatrix(dummyIncidenceMatrix)
                                incidenceTreeMatrix =
                                    rearrangeIncidenceMatrixToGetIncidenceTreeMatrix(
                                        incidenceMatrixP = dummyIncidenceMatrix,
                                        incidenceTreeMatrixP = incidenceTreeMatrix
                                    )
                                listOfIndexesOfBranchespassingThroughNodes = mutableListOf()
                                incidenceTreeMatrix.forEach { incidenceMatricRow ->

                                    checkIfMoreThanLinkPassThroughTheNode(incidenceMatricRow)?.let {

                                        listOfIndexesOfBranchespassingThroughNodes.add(it)

                                    }

                                }

                            }
                            if(listOfIndexesOfBranchespassingThroughNodes.size>1){
                                rowPointer = 1
                                columnPointer = 0
                                return@loop
                                }else
                                return incidenceTreeMatrix

                        }
                        columnPointer++
                    }
                    rowPointer++
                    columnPointer = 0
                }
            }
        }
        return incidenceTreeMatrix
    }

    private fun extractTreeMatrixOverIncidenceMatrix(dummyIncidenceMatrix: MutableList<MutableList<Double>>): MutableList<MutableList<Double>> =
        dummyIncidenceMatrix.map {
            it.subList(0, dummyIncidenceMatrix.size)
        } as MutableList<MutableList<Double>>

    private fun checkIfMoreThanLinkPassThroughTheNode(rowList: List<Double>): MutableList<Int>? {
        val numOfNodesHavingMoreThanBranchPassingThrough: Int =
            rowList.count { (it == 1.0) } + rowList.count { (it == -1.0) }

        var indexesOfPassingThroughBranches: MutableList<Int> = mutableListOf()

        if (numOfNodesHavingMoreThanBranchPassingThrough > 1) {
            rowList.mapIndexed { index, it ->
                if (it == 1.0 || it == -1.0) indexesOfPassingThroughBranches.add(index)
            }
            return indexesOfPassingThroughBranches
        }
        return null
    }

    fun getIncidenceLinkMatrix() {
        for (i in 0 until reducedIncidenceMatrixMutable.size) {
            incidenceLinkMatrix.add(mutableListOf<Double>())
        }
        for (item in orderOfLinksAndBranchedToIncidenceMatrix.withIndex()) {
            if (item.index >= numOfNodes - 1) {
                incidenceLinkMatrix.addColumn(
                    column(
                        item.value,
                        reducedIncidenceMatrixMutable
                    ) as MutableList<Double>
                )
            }
        }
    }

    fun getCLmatrix()  = incidenceTreeMatrix.getInverseMatrixOf_3x3().multiplyWith(incidenceLinkMatrix)

    fun findTieSetMatrix () {
        val Bt = getCLmatrix().multiplyTheMatrixWithNum(-1.0).transpose()
        val ct = getCLmatrix()
        Log.v("cuset size",ct.size.toString())
        val unityMatrix = getUnityMatrix(Bt.size)
        Bt.forEachIndexed { index, mutableList ->
            tieSetMatrix.add(mutableListOf())
            tieSetMatrix[index].addAll(mutableList)
        tieSetMatrix[index].addAll(unityMatrix[index])}


    }

    fun getUnityMatrix(n:Int):MutableList<MutableList<Double>> =
        MutableList(n){ row-> MutableList<Double>(n){ column-> if (row == column) 1.0 else 0.0} }

fun creatImpidenceMatrix(impidenceM:MutableList<MutableList<Double>>):MutableList<MutableList<Double>> {
    return MutableList(impidenceM.size) { row ->
        MutableList<Double>(impidenceM.size) { column -> if (row == column) impidenceM[row][0] else 0.0 }
    }
}
}
