package com.dggorbachev.todoapp.features.tasks_screen.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dggorbachev.todoapp.R
import com.dggorbachev.todoapp.base.util.OnQueryTextChanged
import com.dggorbachev.todoapp.data.SortOrder
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.databinding.FragmentTasksBinding
import com.dggorbachev.todoapp.features.tasks_screen.ui.adapter.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnTaskClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tasksAdapter = TasksAdapter(this)

        binding.rvTasks.apply {
            adapter = tasksAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.tasks.observe(viewLifecycleOwner)
        {
            tasksAdapter.submitList(it)
        }

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.top_bar_menu, menu)

                    val searchItem = menu.findItem(R.id.actionSearch)
                    val searchView = searchItem.actionView as SearchView

                    searchView.OnQueryTextChanged {
                        viewModel.searchQuery.value = it
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        menu.findItem(R.id.actionHideCompleted).isChecked =
                            viewModel.preferencesFlow.first().hideCompleted
                    }
                }

                override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                    return when (menuItem.itemId) {
                        R.id.actionSortByName -> {
                            viewModel.onSortOrderSelected(SortOrder.BY_NAME)
                            true
                        }
                        R.id.actionSortByDate -> {
                            viewModel.onSortOrderSelected(SortOrder.BY_DATE)
                            true
                        }
                        R.id.actionHideCompleted -> {
                            menuItem.isChecked = !menuItem.isChecked
                            viewModel.onHideCompletedClicked(menuItem.isChecked)
                            true
                        }
                        R.id.actionDeleteCompleted -> {
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }

    override fun onTaskClick(taskEntity: TaskEntity) {
        viewModel.onTaskClicked(taskEntity)
    }

    override fun onCheckBoxClick(taskEntity: TaskEntity, isChecked: Boolean) {
        viewModel.onTaskCompletedChanged(taskEntity, isChecked)
    }
}