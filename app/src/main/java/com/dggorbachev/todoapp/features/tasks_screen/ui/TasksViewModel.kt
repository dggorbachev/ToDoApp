package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.*
import com.dggorbachev.todoapp.base.common.Constants.ADD_TASK_RESULT_OK
import com.dggorbachev.todoapp.base.common.Constants.EDIT_TASK_RESULT_OK
import com.dggorbachev.todoapp.features.tasks_screen.PreferencesManager
import com.dggorbachev.todoapp.features.tasks_screen.SortOrder
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class TasksViewModel @Inject constructor(
    state: SavedStateHandle,
    private val tasksInteractor: TasksInteractor,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow: Flow<List<TaskEntity>> = combine(searchQuery.asFlow(), preferencesFlow)
    { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        tasksInteractor.read(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }

    fun onHideCompletedClick(hideCompleted: Boolean) =
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }

    fun onTaskClick(taskEntity: TaskEntity) =
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskFragment(taskEntity))
        }

    fun onTaskCompletedChanged(taskEntity: TaskEntity, isCompleted: Boolean) =
        viewModelScope.launch {
            tasksInteractor.update(taskEntity.copy(isCompleted = isCompleted))
        }

    fun onTaskSwiped(taskEntity: TaskEntity) =
        viewModelScope.launch {
            tasksInteractor.delete(taskEntity)
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(taskEntity))
        }

    fun onUndoDeleteClick(taskEntity: TaskEntity) =
        viewModelScope.launch {
            tasksInteractor.insert(taskEntity)
        }

    fun onAddNewTaskClick() =
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToNewTaskFragment)
        }

    fun onDetailsResult(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) =
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(message))
        }

    fun onDeleteCompletedClick() =
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToDeleteCompletedFragment)
        }

    sealed class TasksEvent {
        object NavigateToNewTaskFragment : TasksEvent()
        object NavigateToDeleteCompletedFragment : TasksEvent()
        data class NavigateToEditTaskFragment(val taskEntity: TaskEntity) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val taskEntity: TaskEntity) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val message: String) : TasksEvent()
    }
}