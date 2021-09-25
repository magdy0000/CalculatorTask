package com.example.calculatortask.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class OperationModel(
    @PrimaryKey
    val id : Long ,
    val date : String ,
    val numbers : String ,
    val operator : String,
    val result : String = ""
)
