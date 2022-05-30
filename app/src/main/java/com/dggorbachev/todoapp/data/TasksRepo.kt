package com.dggorbachev.todoapp.data

import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import com.dggorbachev.todoapp.data.local.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TasksRepo {
    suspend fun insert(taskEntity: TaskEntity)
    fun read(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TaskEntity>>
    suspend fun update(taskEntity: TaskEntity)
    suspend fun delete(taskEntity: TaskEntity)
}