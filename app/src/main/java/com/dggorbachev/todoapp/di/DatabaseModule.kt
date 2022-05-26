package com.dggorbachev.todoapp.di

import android.app.Application
import androidx.room.Room
import com.dggorbachev.todoapp.AppDataBase
import com.dggorbachev.todoapp.base.common.Constants.DATA_BASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: AppDataBase.Callback
    ) = Room.databaseBuilder(app, AppDataBase::class.java, DATA_BASE)
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideTaskDao(db: AppDataBase) = db.tasksDAO()
}
