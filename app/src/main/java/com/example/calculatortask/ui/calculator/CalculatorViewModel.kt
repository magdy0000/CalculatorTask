package com.example.calculatortask.ui.calculator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculatortask.data.local.MyDao
import com.example.calculatortask.data.models.OperationModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class CalculatorViewModel
@Inject constructor(private val myDao: MyDao) : ViewModel() {


    private val _statusLiveData = MutableLiveData<String>()
    val statusLiveData get() = _statusLiveData
    fun addOperation(operationModel: OperationModel) {

        viewModelScope.launch(IO) {

            try {
                myDao.insertOperation(operationModel)
                _statusLiveData.postValue("success")
            }catch (e : Exception){

                _statusLiveData.postValue("fail")
            }

        }



    }
}