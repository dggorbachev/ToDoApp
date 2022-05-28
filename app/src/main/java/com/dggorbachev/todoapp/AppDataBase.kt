package com.dggorbachev.todoapp

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dggorbachev.todoapp.data.local.TasksDAO
import com.dggorbachev.todoapp.data.local.TaskEntity

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun tasksDAO(): TasksDAO
}