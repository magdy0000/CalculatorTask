package com.example.calculatortask.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.calculatortask.data.models.OperationModel


@Database(entities = [OperationModel::class], version =1)
abstract class  MyDataBase  : RoomDatabase() {
    abstract fun getMyDao(): MyDao


}