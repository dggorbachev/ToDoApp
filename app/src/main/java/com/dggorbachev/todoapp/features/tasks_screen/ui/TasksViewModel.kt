package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.data.local.TasksDAO
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksInteractor: TasksInteractor
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val tasksFlow: Flow<List<TaskEntity>> = combine(searchQuery, sortOrder, hideCompleted)
    { query, sortOrder, hideCompleted ->
        Triple(query, sortOrder, hideCompleted)
    }.flatMapLatest { (query, sortOrder, hideCompleted) ->
        tasksInteractor.read(query, sortOrder, hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()
}

enum class SortOrder { BY_NAME, BY_DATE }