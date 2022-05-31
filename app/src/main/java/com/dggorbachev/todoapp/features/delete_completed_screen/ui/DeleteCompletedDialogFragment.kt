package com.dggorbachev.todoapp.features.delete_completed_screen.ui

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteCompletedDialogFragment : DialogFragment() {

    private val viewModel: DeleteCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm delete")
            .setMessage("Are you sure to delete completed tasks?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Accept") { _, _ ->
                viewModel.onAcceptClick()
            }
            .create()
}