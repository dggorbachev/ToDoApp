package com.dggorbachev.todoapp.features.tasks_screen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.databinding.ItemTaskBinding


class TasksAdapter : ListAdapter<TaskEntity, TasksAdapter.ViewHolder>(DiffCallback()) {
    class ViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(taskEntity: TaskEntity) {
            binding.apply {
                tvTaskName.text = taskEntity.name
                cbCompleted.isChecked = taskEntity.isCompleted
                tvTaskName.paint.isStrikeThruText = taskEntity.isCompleted
                ivPriority.isVisible = taskEntity.isImportant
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val curItem = getItem(position)
        holder.bind(curItem)
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}