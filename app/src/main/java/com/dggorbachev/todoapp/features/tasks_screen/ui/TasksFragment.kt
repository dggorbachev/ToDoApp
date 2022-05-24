package com.dggorbachev.todoapp.features.tasks_screen.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dggorbachev.todoapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

    private val viewModel: TasksViewModel by viewModels()
}