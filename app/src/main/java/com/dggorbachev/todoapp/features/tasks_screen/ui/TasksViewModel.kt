package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.dggorbachev.todoapp.data.local.TasksDAO
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksDAO: TasksDAO
) : ViewModel() {

    val tasks = tasksDAO.read().asLiveData()
}