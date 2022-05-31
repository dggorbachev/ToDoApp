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
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "isImportant")
    val isImportant: Boolean = false,
    @ColumnInfo(name = "postDate")
    val postDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "isCompleted")
    val isCompleted: Boolean = false,
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0
) : Parcelable {
    companion object {
        const val TABLE_NAME = "TASK_TABLE"
    }

    val postDateFormatted: String
        get() = DateFormat.getDateTimeInstance().format(postDate)
}