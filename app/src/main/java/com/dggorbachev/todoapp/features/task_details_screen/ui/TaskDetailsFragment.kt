package com.dggorbachev.todoapp.features.task_details_screen.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.dggorbachev.todoapp.R
import com.dggorbachev.todoapp.base.util.exhaustive
import com.dggorbachev.todoapp.databinding.FragmentTaskDetailsBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TaskDetailsFragment : Fragment(R.layout.fragment_task_details) {

    private val viewModel: TaskDetailsViewModel by viewModels()
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            etTaskName.setText(viewModel.taskName)
            cbImportantTask.isChecked = viewModel.taskIsImportant
            cbImportantTask.jumpDrawablesToCurrentState()
            tvPostDate.isVisible = viewModel.task != null
            tvPostDate.text = "Date created: ${viewModel.task?.postDateFormatted}"

            etTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            cbImportantTask.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskIsImportant = isChecked
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClicked()
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.detailsTaskEvent.collect { event ->
                when (event) {
                    is TaskDetailsViewModel.DetailsTaskEvent.NavigateBackWithResult -> {
                        binding.etTaskName.clearFocus()
                        setFragmentResult(
                            "details_request",
                            bundleOf("details_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                    is TaskDetailsViewModel.DetailsTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                    }
                }.exhaustive
            }
        }
    }
}