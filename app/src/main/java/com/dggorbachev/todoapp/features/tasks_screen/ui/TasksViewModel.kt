package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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
    private val tasksInteractor: TasksInteractor,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow: Flow<List<TaskEntity>> = combine(searchQuery, preferencesFlow)
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

    fun onHideCompletedClicked(hideCompleted: Boolean) =
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }

    fun onTaskClicked(taskEntity: TaskEntity) {
        TODO("Add functional")
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

    fun onUndoDeleteClick(taskEntity: TaskEntity) {
        viewModelScope.launch {
            tasksInteractor.insert(taskEntity)
        }
    }

    sealed class TasksEvent {
        data class ShowUndoDeleteTaskMessage(val taskEntity: TaskEntity) : TasksEvent()
    }
}