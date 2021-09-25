package com.example.calculatortask.di

import android.content.Context
import androidx.room.Room
import com.example.calculatortask.data.local.MyDao
import com.example.calculatortask.data.local.MyDataBase
import com.example.calculatortask.utils.Const
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object Module {

    @Provides
    @Singleton
    fun provideRoomDatabase(@ApplicationContext appContext: Context): MyDataBase {
        return Room
            .databaseBuilder(appContext, MyDataBase::class.java, Const.DATABASE_NAME)
             .fallbackToDestructiveMigration()
            .build()
    }
    @Provides
    @Singleton
    fun provideDao (localDatabase: MyDataBase) : MyDao {
        return localDatabase.getMyDao()
    }


}