package com.example.calculatortask.ui.calculator

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.example.calculatortask.R
import com.example.calculatortask.OperationWorker
import com.example.calculatortask.data.models.OperationModel
import com.example.calculatortask.databinding.FragmentCalculatorBinding
import com.example.calculatortask.utils.Const
import com.example.calculatortask.utils.Helper
import com.example.calculatortask.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class CalculatorFragment : Fragment() {

    private var _binding: FragmentCalculatorBinding? = null
    private val timeFormat = SimpleDateFormat("hh:mm:ss")
    private val calender = Calendar.getInstance()
    private var operator : String ?= null
    private var numbers : String?= null
    private var time : Long ?= null
    private var date : String ?= null
    private var operationId : Long ?= null
    private val binding get() = _binding!!

    private val calculatorViewModel : CalculatorViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_calculator, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCalculatorBinding.bind(view)
        onClicks()
        observers()
    }
    private fun observers(){

        calculatorViewModel.statusLiveData.observe(viewLifecycleOwner, {
            showToast(it)
        })
    }

    private fun onClicks() {

        binding.apply {
            radioGroup.setOnCheckedChangeListener { p0, p1 ->

                val index = radioGroup.indexOfChild(radioGroup.findViewById(p1))
                operator = Helper.getOperator(index)
            }
            btnExecute.setOnClickListener {
                validation()
            }


        }

    }

    private fun validation() {
        binding.apply {
           numbers = editNumbers.text.toString().trim()

            when {
                numbers?.isEmpty()!! -> {
                    editNumbers.error = getString(R.string.required)
                }
                operator == null -> {

                    showToast(getString(R.string.operator_validation))
                }
                spinnerTime.selectedItemPosition == 0 -> {
                    showToast(getString(R.string.time_validation))
                }
                else -> {
                   setScheduleOperation()
                }
            }
        }
    }

    private fun setScheduleOperation (){
        time = Helper.getTime(binding.spinnerTime.selectedItemPosition).toLong()
        operationId = System.currentTimeMillis()
        calender.add(Calendar.SECOND, time?.toInt()!!)
        date = timeFormat.format(calender.time)
        val data = Data.Builder()
        data.putLong(Const.OPERATION_ID, operationId!!)
        data.putString(Const.OPERATION_OPERATOR, operator!!)
        data.putString(Const.OPERATION_NUMBERS, numbers!!)
        val  mywork=
            OneTimeWorkRequest.Builder(OperationWorker::class.java)
                .setInitialDelay(time!!, TimeUnit.SECONDS)
                .setInputData(data.build())
                .build()
        WorkManager.getInstance(requireActivity()).enqueue(mywork)

        val operation = OperationModel(operationId!!, date!!, numbers!!, operator!!)
        calculatorViewModel.addOperation(operation)



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}