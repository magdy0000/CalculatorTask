package com.example.calculatortask.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.calculatortask.data.models.OperationModel


@Dao
interface MyDao {



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(model: OperationModel)


    @Query("select * from OperationModel")
    suspend fun getOperation () : List<OperationModel>

    @Query("UPDATE OperationModel SET result = :result  WHERE id LIKE :id")
    suspend fun setResult(result: String, id: Long)

}