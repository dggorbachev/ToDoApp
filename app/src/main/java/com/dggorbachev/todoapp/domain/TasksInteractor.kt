package com.dggorbachev.todoapp.domain

import androidx.lifecycle.LiveData
import com.dggorbachev.todoapp.base.attempt
import com.dggorbachev.todoapp.data.TasksRepo
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.features.tasks_screen.ui.SortOrder
import kotlinx.coroutines.flow.Flow

class TasksInteractor(private val repo: TasksRepo) {
    suspend fun insert(taskEntity: TaskEntity) =
        repo.insert(taskEntity = taskEntity)

    fun read(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TaskEntity>> =
        repo.read(query, sortOrder, hideCompleted)

    suspend fun update(taskEntity: TaskEntity) =
        repo.update(taskEntity = taskEntity)

    suspend fun delete(taskEntity: TaskEntity) =
        attempt { repo.delete(taskEntity = taskEntity) }
}