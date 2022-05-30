package com.dggorbachev.todoapp.features.tasks_screen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dggorbachev.todoapp.data.local.TaskEntity
import com.dggorbachev.todoapp.databinding.ItemTaskBinding


class TasksAdapter(private val listener: OnTaskClickListener) :
    ListAdapter<TaskEntity, TasksAdapter.ViewHolder>(DiffCallback()) {
    inner class ViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            with(binding) {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onTaskClick(task)
                    }

                }
                cbCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val task = getItem(position)
                        listener.onCheckBoxClick(task, cbCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(taskEntity: TaskEntity) {
            with(binding) {
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

    interface OnTaskClickListener {
        fun onTaskClick(taskEntity: TaskEntity)
        fun onCheckBoxClick(taskEntity: TaskEntity, isChecked: Boolean)
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