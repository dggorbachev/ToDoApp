package com.dggorbachev.todoapp.features.tasks_screen.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dggorbachev.todoapp.R
import com.dggorbachev.todoapp.base.util.OnQueryTextChanged
import com.dggorbachev.todoapp.base.util.exhaustive
import com.dggorbachev.todoapp.features.tasks_screen.SortOrder
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.databinding.FragmentTasksBinding
import com.dggorbachev.todoapp.features.tasks_screen.ui.adapter.TasksAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks), TasksAdapter.OnTaskClickListener {

    private val viewModel: TasksViewModel by viewModels()
    private var _binding: FragmentTasksBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView

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

        // Swipe to delete
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val task = tasksAdapter.currentList[viewHolder.adapterPosition]
                viewModel.onTaskSwiped(task)
            }
        }).attachToRecyclerView(binding.rvTasks)

        binding.fabAddTask.setOnClickListener {
            viewModel.onAddNewTaskClick()
        }

        //1
        // Logic of events
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.tasksEvent.collect { event ->
                when (event) {
                    is TasksViewModel.TasksEvent.ShowUndoDeleteTaskMessage -> {
                        Snackbar.make(requireView(), "Task deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO") {
                                viewModel.onUndoDeleteClick(event.taskEntity)
                            }.show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToEditTaskFragment -> {
                        val action =
                            TasksFragmentDirections
                                .actionTasksFragmentToTaskDetailsFragment(
                                    "Edit task",
                                    event.taskEntity
                                )
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.NavigateToNewTaskFragment -> {
                        val action =
                            TasksFragmentDirections.actionTasksFragmentToTaskDetailsFragment("Add task")
                        findNavController().navigate(action)
                    }
                    is TasksViewModel.TasksEvent.ShowTaskSavedConfirmationMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_SHORT).show()
                    }
                    is TasksViewModel.TasksEvent.NavigateToDeleteCompletedFragment -> {
                        val action =
                            TasksFragmentDirections.actionGlobalDeleteCompletedDialogFragment()
                        findNavController().navigate(action)
                    }
                }.exhaustive
            }
        }

        // Get the value of the fragment from which we came
        setFragmentResultListener("details_request") { _, bundle ->
            val result = bundle.getInt("details_result")
            viewModel.onDetailsResult(result)
        }

        // Submit list to adapter when it changes
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }

        // Main features: search, sort, hide, delete
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(
            object : MenuProvider {
                override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                    menuInflater.inflate(R.menu.top_bar_menu, menu)

                    val searchItem = menu.findItem(R.id.actionSearch)
                    searchView = searchItem.actionView as SearchView

                    val pendingQuery = viewModel.searchQuery.value
                    if (pendingQuery != null && pendingQuery.isNotEmpty()) {
                        searchItem.expandActionView()
                        searchView.setQuery(pendingQuery, false)
                    }

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
                            viewModel.onHideCompletedClick(menuItem.isChecked)
                            true
                        }
                        R.id.actionDeleteCompleted -> {
                            viewModel.onDeleteCompletedClick()
                            true
                        }
                        else -> false
                    }
                }
            }, viewLifecycleOwner, Lifecycle.State.RESUMED
        )
    }

    override fun onTaskClick(taskEntity: TaskEntity) {
        viewModel.onTaskClick(taskEntity)
    }

    override fun onCheckBoxClick(taskEntity: TaskEntity, isChecked: Boolean) {
        viewModel.onTaskCompletedChanged(taskEntity, isChecked)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        searchView.setOnQueryTextListener(null)
    }
}
