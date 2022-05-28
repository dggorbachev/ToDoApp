package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dggorbachev.todoapp.data.local.TasksDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksDAO: TasksDAO
) : ViewModel() {

    val searchQuery = MutableStateFlow("")

    private val tasksFlow = searchQuery.flatMapLatest {
        tasksDAO.read(it)
    }

    val tasks = tasksFlow.asLiveData()
}