package com.dggorbachev.todoapp.data.di

import com.dggorbachev.todoapp.data.TasksRepo
import com.dggorbachev.todoapp.data.TasksRepoImpl
import com.dggorbachev.todoapp.data.local.TasksDAO
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TaskModule {

    @Provides
    @Singleton
    fun provideTasksRepo(tasksDAO: TasksDAO): TasksRepo =
        TasksRepoImpl(tasksDAO = tasksDAO)

    @Provides
    @Singleton
    fun provideTasksInteractor(repo: TasksRepo): TasksInteractor =
        TasksInteractor(repo = repo)
}