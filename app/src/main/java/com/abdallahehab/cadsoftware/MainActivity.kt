package com.abdallahehab.cadsoftware

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.abdallahehab.cadsoftware.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var  showResult:Boolean = false

    lateinit var  binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.btnCalc.setOnClickListener {
//            binding.incidenceMatrixInput.visibility = View.GONE
//            val matrix = IncidenceMatrix(binding.incidenceMatrixInput.editText!!.text.toString())
//            binding.arrangedIncidenceMatrixText.text = "${matrix.tieSetMatrix}\n"
////            matrix.getIncidenceLinkMatrix()
//
////            var mutableList  = mutableListOf(mutableListOf<Int>(3, 0,2), mutableListOf<Int>(2,0,-2), mutableListOf<Int>(0,1,1))
////            binding.arrangedIncidenceMatrixText.text = "${mutableList.getInverseMatrix()}"
//
////                        binding.arrangedIncidenceMatrixText.text = "${(555550.34675).round()}"
////
//        }
        val onClickListener = binding.calcBtn.setOnClickListener {
            val incidenceMatrix = binding.incidenceMatrixInput.editText!!.text.toString()
                .convertStringMatrixToNumricMatrix()

            val incidenceMatrixObj = IncidenceMatrix(incidenceMatrix)

            val currentMatrix = binding.currentMatrixInput.editText!!.text.toString()
                .convertStringMatrixToNumricMatrix().rearrangeMatrix(incidenceMatrixObj.orderOfLinksAndBranchedToIncidenceMatrix)
            var impidenceMatrix = binding.impidenceMatrixInput.editText!!.text.toString()
                .convertStringMatrixToNumricMatrix().rearrangeMatrix(incidenceMatrixObj.orderOfLinksAndBranchedToIncidenceMatrix)
            impidenceMatrix = incidenceMatrixObj.creatImpidenceMatrix(impidenceMatrix)
            val voltagesMatrix = binding.voltageMatrixInput.editText!!.text.toString()
                .convertStringMatrixToNumricMatrix().rearrangeMatrix(incidenceMatrixObj.orderOfLinksAndBranchedToIncidenceMatrix)

            val tiesetMatrix = incidenceMatrixObj.tieSetMatrix

            val ZBt =  impidenceMatrix.multiplyWith(tiesetMatrix.transpose())
            val ZI = impidenceMatrix.multiplyWith(currentMatrix)
            val BE = tiesetMatrix.multiplyWith(voltagesMatrix)
            val BZBt = tiesetMatrix.multiplyWith(ZBt)
            val BZI = tiesetMatrix.multiplyWith(ZI)
            val BE_BZI = BE.mapIndexed { rowIndex, mutableList -> mutableList.mapIndexed { columnIndex, d -> d-BZI[rowIndex][columnIndex] }.toMutableList() }.toMutableList()
            val loopCurrent:MutableList<MutableList<Double>>
            if(BZBt.size == 2){
                 loopCurrent = BZBt.getInverseMatrixOf_2x2().multiplyWith(BE_BZI)

            }else{
                loopCurrent = BZBt.getInverseMatrixOf_3x3().multiplyWith(BE_BZI)

            }


            val branchCurrentMatrix = tiesetMatrix.transpose().multiplyWith(loopCurrent).map { itis-> itis.map { it.round(2) }.toMutableList() }.toMutableList()
            binding.arrangedIncidenceMatrixText.text = "Jb:\n${branchCurrentMatrix.restoreArrangmentOfMatrix(incidenceMatrixObj.orderOfLinksAndBranchedToIncidenceMatrix).inflateMtrix()}"
            toggleResultModeAndInputMode()
        }

        binding.resetBtn.setOnClickListener {
            toggleResultModeAndInputMode()
        }

    }

    private fun toggleResultModeAndInputMode(){
        showResult = !showResult
        if (showResult) {
            binding.incidenceMatrixInput.visibility = View.GONE
            binding.currentMatrixInput.visibility = View.GONE
            binding.impidenceMatrixInput.visibility = View.GONE
            binding.voltageMatrixInput.visibility = View.GONE
            binding.calcBtn.visibility = View.GONE

            binding.arrangedIncidenceMatrixText.visibility = View.VISIBLE
            binding.resetBtn.visibility = View.VISIBLE

        }else{
            binding.incidenceMatrixInput.visibility = View.VISIBLE
            binding.currentMatrixInput.visibility = View.VISIBLE
            binding.impidenceMatrixInput.visibility = View.VISIBLE
            binding.voltageMatrixInput.visibility = View.VISIBLE
            binding.calcBtn.visibility = View.VISIBLE

            binding.arrangedIncidenceMatrixText.visibility = View.GONE
            binding.resetBtn.visibility = View.GONE

        }

    }
}