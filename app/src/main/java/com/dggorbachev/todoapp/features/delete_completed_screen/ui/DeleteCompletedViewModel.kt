package com.dggorbachev.todoapp.features.delete_completed_screen.ui

import androidx.lifecycle.ViewModel
import com.dggorbachev.todoapp.di.ApplicationScope
import com.dggorbachev.todoapp.domain.TasksInteractor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteCompletedViewModel @Inject constructor(
    private val tasksInteractor: TasksInteractor,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onAcceptClick() = applicationScope.launch {
        tasksInteractor.deleteCompletedTasks()
    }
}