package com.dggorbachev.todoapp.data

import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.features.tasks_screen.SortOrder
import kotlinx.coroutines.flow.Flow

interface TasksRepo {
    suspend fun insert(taskEntity: TaskEntity)
    fun read(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TaskEntity>>
    suspend fun update(taskEntity: TaskEntity)
    suspend fun delete(taskEntity: TaskEntity)
    suspend fun deleteCompletedTasks()
}