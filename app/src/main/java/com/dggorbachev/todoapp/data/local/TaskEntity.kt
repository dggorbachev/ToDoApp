package com.dggorbachev.todoapp.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dggorbachev.todoapp.data.local.TaskEntity.Companion.TABLE_NAME
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = TABLE_NAME)
@Parcelize
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "postDate")
    val postDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "isImportant")
    val isImportant: Boolean,
    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean
) : Parcelable {
    companion object {
        const val TABLE_NAME = "TASK_TABLE"
    }

    val postDateFormatted: String
        get() = DateFormat.getTimeInstance().format(postDate)
}