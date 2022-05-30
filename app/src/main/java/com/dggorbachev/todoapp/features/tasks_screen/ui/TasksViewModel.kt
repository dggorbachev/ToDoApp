package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dggorbachev.todoapp.base.BaseViewModel
import com.dggorbachev.todoapp.base.Event
import com.dggorbachev.todoapp.data.PreferencesManager
import com.dggorbachev.todoapp.data.SortOrder
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksInteractor: TasksInteractor,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksFlow: Flow<List<TaskEntity>> = combine(searchQuery, preferencesFlow)
    { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        tasksInteractor.read(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) =
        viewModelScope.launch {
            preferencesManager.updateSortOrder(sortOrder)
        }

    fun onHideCompletedClicked(hideCompleted: Boolean) =
        viewModelScope.launch {
            preferencesManager.updateHideCompleted(hideCompleted)
        }

    val tasks = tasksFlow.asLiveData()
}