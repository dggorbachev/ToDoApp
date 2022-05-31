package com.dggorbachev.todoapp.data

import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.data.local.TasksDAO
import com.dggorbachev.todoapp.features.tasks_screen.SortOrder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TasksRepoImpl
@Inject constructor(private val tasksDAO: TasksDAO) : TasksRepo {
    override suspend fun insert(taskEntity: TaskEntity) {
        tasksDAO.insert(taskEntity)
    }

    override fun read(
        query: String,
        sortOrder: SortOrder,
        hideCompleted: Boolean
    ): Flow<List<TaskEntity>> {
        return tasksDAO.read(query, sortOrder, hideCompleted)
    }

    override suspend fun update(taskEntity: TaskEntity) {
        tasksDAO.update(taskEntity)
    }

    override suspend fun delete(taskEntity: TaskEntity) {
        tasksDAO.delete(taskEntity)
    }

    override suspend fun deleteCompletedTasks() {
        tasksDAO.deleteCompletedTasks()
    }
}