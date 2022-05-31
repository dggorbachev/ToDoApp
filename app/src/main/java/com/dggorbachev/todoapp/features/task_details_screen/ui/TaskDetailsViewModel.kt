package com.dggorbachev.todoapp.features.task_details_screen.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dggorbachev.todoapp.base.common.Constants.ADD_TASK_RESULT_OK
import com.dggorbachev.todoapp.base.common.Constants.EDIT_TASK_RESULT_OK
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailsViewModel
@Inject constructor(
    private val state: SavedStateHandle,
    private val tasksInteractor: TasksInteractor
) : ViewModel() {

    val task = state.get<TaskEntity>("taskEntity")

    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state["taskName"] = value
        }

    var taskIsImportant = state.get<Boolean>("taskIsImportant") ?: task?.isImportant ?: false
        set(value) {
            field = value
            state["taskIsImportant"] = value
        }

    private val detailsTaskEventChannel = Channel<DetailsTaskEvent>()
    val detailsTaskEvent = detailsTaskEventChannel.receiveAsFlow()

    fun onSaveClicked() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name can't be empty or blank")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, isImportant = taskIsImportant)
            updateTask(updatedTask)
        } else {
            val newTask = TaskEntity(name = taskName, isImportant = taskIsImportant)
            createTask(newTask)
        }
    }

    private fun createTask(newTask: TaskEntity) =
        viewModelScope.launch {
            tasksInteractor.insert(newTask)
            detailsTaskEventChannel.send(DetailsTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
        }

    private fun updateTask(updatedTask: TaskEntity) =
        viewModelScope.launch {
            tasksInteractor.update(updatedTask)
            detailsTaskEventChannel.send(DetailsTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
        }

    private fun showInvalidInputMessage(message: String) =
        viewModelScope.launch {
            detailsTaskEventChannel.send(DetailsTaskEvent.ShowInvalidInputMessage(message))
        }

    sealed class DetailsTaskEvent {
        data class ShowInvalidInputMessage(val message: String) : DetailsTaskEvent()
        data class NavigateBackWithResult(val result: Int) : DetailsTaskEvent()
    }
}