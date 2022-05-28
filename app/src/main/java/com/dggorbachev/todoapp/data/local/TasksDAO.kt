package com.dggorbachev.todoapp.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskEntity: TaskEntity)

    @Query("SELECT * FROM task_table WHERE name LIKE '%' || :searchQuery || '%' ORDER BY isImportant DESC")
    fun read(searchQuery: String): Flow<List<TaskEntity>>

    @Update
    suspend fun update(taskEntity: TaskEntity)

    @Delete
    suspend fun delete(taskEntity: TaskEntity)
}