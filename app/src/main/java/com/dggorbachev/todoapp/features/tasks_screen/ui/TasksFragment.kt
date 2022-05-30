package com.dggorbachev.todoapp.features.tasks_screen.ui

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.dggorbachev.todoapp.R
import com.dggorbachev.todoapp.base.util.OnQueryTextChanged
import com.dggorbachev.todoapp.databinding.FragmentTasksBinding
import com.dggorbachev.todoapp.features.tasks_screen.ui.adapter.TasksAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TasksFragment : Fragment(R.layout.fragment_tasks) {

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

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.top_bar_menu, menu)

                val searchItem = menu.findItem(R.id.actionSearch)
                val searchView = searchItem.actionView as SearchView

                searchView.OnQueryTextChanged {
                    viewModel.searchQuery.value = it
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.actionSortByName -> {
                        viewModel.sortOrder.value = SortOrder.BY_NAME
                        true
                    }
                    R.id.actionSortByDate -> {
                        viewModel.sortOrder.value = SortOrder.BY_DATE
                        true
                    }
                    R.id.actionHideCompleted -> {
                        menuItem.isChecked = !menuItem.isChecked
                        viewModel.hideCompleted.value = menuItem.isChecked
                        true
                    }
                    R.id.actionDeleteCompleted -> {
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        val tasksAdapter = TasksAdapter()

        binding.apply {
            rvTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
        }
    }
}