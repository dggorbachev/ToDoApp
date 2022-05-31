package com.dggorbachev.todoapp.data.local

import androidx.room.*
import com.dggorbachev.todoapp.features.tasks_screen.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskEntity: TaskEntity)

    fun read(query: String, sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<TaskEntity>> =
        when (sortOrder) {
            SortOrder.BY_NAME -> readTasksSortedByName(query, hideCompleted)
            SortOrder.BY_DATE -> readTasksSortedByDate(query, hideCompleted)
        }

    @Query("SELECT * FROM task_table WHERE (isCompleted!=:hideCompleted OR isCompleted = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, name")
    fun readTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_table WHERE (isCompleted!=:hideCompleted OR isCompleted = 0) AND name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC, postDate")
    fun readTasksSortedByDate(searchQuery: String, hideCompleted: Boolean): Flow<List<TaskEntity>>

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Delete
    suspend fun delete(taskEntity: TaskEntity)
}