package com.dggorbachev.todoapp.di

import android.content.Context
import androidx.room.Room
import com.dggorbachev.todoapp.AppDataBase
import com.dggorbachev.todoapp.base.common.Constants.DATA_BASE
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): AppDataBase =
        Room.databaseBuilder(appContext, AppDataBase::class.java, DATA_BASE)
            .createFromAsset("init_db.db")
            .build()

    @Provides
    fun provideTaskDao(db: AppDataBase) = db.tasksDAO()
}