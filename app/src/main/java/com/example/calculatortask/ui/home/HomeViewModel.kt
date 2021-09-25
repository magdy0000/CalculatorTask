package com.example.calculatortask.ui.home

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
class HomeViewModel
@Inject constructor(private val myDao: MyDao) : ViewModel() {


    private val _operationsLiveData = MutableLiveData<List<OperationModel>>()
    val operationsLiveData get() = _operationsLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData get() = _errorLiveData

    fun getAllOperations() {

        viewModelScope.launch(IO) {

            try {
              val data =   myDao.getOperation()
                _operationsLiveData.postValue(data)
            }catch (e : Exception){

                _errorLiveData.postValue(e.localizedMessage)
            }

        }
    }
}