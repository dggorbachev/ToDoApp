package com.dggorbachev.todoapp.di

import android.app.Application
import androidx.room.Room
import com.dggorbachev.todoapp.AppDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application
    ) = Room.databaseBuilder(app, AppDataBase::class.java, "task_database")
        .createFromAsset("init_db.db")
        .build()

    @Provides
    fun provideTaskDao(db: AppDataBase) = db.tasksDAO()
}