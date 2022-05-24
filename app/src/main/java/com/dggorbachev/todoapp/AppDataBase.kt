package com.dggorbachev.todoapp

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dggorbachev.todoapp.data.local.TasksDAO
import com.dggorbachev.todoapp.data.local.TaskEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@Database(entities = [TaskEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun tasksDAO(): TasksDAO

    class Callback @Inject constructor(
        private val database: Provider<AppDataBase>,
        private val applicationScope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            val dao = database.get().tasksDAO()

            applicationScope.launch {
                dao.insert(TaskEntity("Buy a notebook", isImportant = true))
                dao.insert(TaskEntity("Call grandma", isImportant = true))
                dao.insert(TaskEntity("Go to the gym"))
                dao.insert(TaskEntity("Wash the dishes", isCompleted = true))
                dao.insert(TaskEntity("Wipe the dust in your room"))
                dao.insert(TaskEntity("Watch the video tutorial", isCompleted = true))
                dao.insert(TaskEntity("Think about the name of the new note and write it down in your ToDoApp"))
            }
        }
    }
}