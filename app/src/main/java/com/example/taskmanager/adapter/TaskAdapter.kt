package com.example.taskmanager.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.model.Task

class TaskAdapter(
    private var taskList: MutableList<Task>,
    private val context: Context,  // Pass the context for toast and other UI updates
    private val onUpdateClick: (Task, String, String, String, String) -> Unit,  // Function to handle Update button clicks with editable data
    private val onDeleteClick: (Task) -> Unit   // Function to handle Delete button clicks
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.task_title)
        val descriptionTextView: TextView = itemView.findViewById(R.id.task_description)
        val startDateTextView: TextView = itemView.findViewById(R.id.task_start_date)
        val endDateTextView: TextView = itemView.findViewById(R.id.task_end_date)
        val updateButton: Button = itemView.findViewById(R.id.update_button)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)

        // Editable fields for updating task
        val editTaskTitle: EditText = itemView.findViewById(R.id.edit_task_title)
        val editTaskDescription: EditText = itemView.findViewById(R.id.edit_task_description)
        val editTaskStartDate: EditText = itemView.findViewById(R.id.edit_task_start_date)
        val editTaskEndDate: EditText = itemView.findViewById(R.id.edit_task_end_date)
        val saveUpdateButton: Button = itemView.findViewById(R.id.save_update_button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        // Set task details
        holder.titleTextView.text = task.title
        holder.descriptionTextView.text = task.description
        holder.startDateTextView.text = "Start: ${task.startDate}"
        holder.endDateTextView.text = "End: ${task.endDate}"

        // Initially hide the editable fields and save button
        holder.editTaskTitle.visibility = View.GONE
        holder.editTaskDescription.visibility = View.GONE
        holder.editTaskStartDate.visibility = View.GONE
        holder.editTaskEndDate.visibility = View.GONE
        holder.saveUpdateButton.visibility = View.GONE

        // Toggle visibility of task details when title is clicked
        holder.titleTextView.setOnClickListener {
            if (holder.descriptionTextView.visibility == View.GONE) {
                holder.descriptionTextView.visibility = View.VISIBLE
                holder.startDateTextView.visibility = View.VISIBLE
                holder.endDateTextView.visibility = View.VISIBLE
            } else {
                holder.descriptionTextView.visibility = View.GONE
                holder.startDateTextView.visibility = View.GONE
                holder.endDateTextView.visibility = View.GONE
            }
        }

        // Handle Update button click
        holder.updateButton.setOnClickListener {
            // Show editable fields and save button
            holder.editTaskTitle.setText(task.title)
            holder.editTaskDescription.setText(task.description)
            holder.editTaskStartDate.setText(task.startDate)
            holder.editTaskEndDate.setText(task.endDate)

            holder.editTaskTitle.visibility = View.VISIBLE
            holder.editTaskDescription.visibility = View.VISIBLE
            holder.editTaskStartDate.visibility = View.VISIBLE
            holder.editTaskEndDate.visibility = View.VISIBLE
            holder.saveUpdateButton.visibility = View.VISIBLE
        }

        // Handle Save Update button click
        holder.saveUpdateButton.setOnClickListener {
            // Collect updated data from EditTexts
            val updatedTitle = holder.editTaskTitle.text.toString()
            val updatedDescription = holder.editTaskDescription.text.toString()
            val updatedStartDate = holder.editTaskStartDate.text.toString()
            val updatedEndDate = holder.editTaskEndDate.text.toString()

            // Trigger the update action with updated data
            onUpdateClick(task, updatedTitle, updatedDescription, updatedStartDate, updatedEndDate)

            // Hide the EditTexts and Save button after update
            holder.editTaskTitle.visibility = View.GONE
            holder.editTaskDescription.visibility = View.GONE
            holder.editTaskStartDate.visibility = View.GONE
            holder.editTaskEndDate.visibility = View.GONE
            holder.saveUpdateButton.visibility = View.GONE
        }

        // Handle Delete button click
        holder.deleteButton.setOnClickListener {
            // Trigger the delete action
            onDeleteClick(task)
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    // Method to update tasks
    fun updateTasks(newTasks: List<Task>) {
        taskList.clear()
        taskList.addAll(newTasks)
        notifyDataSetChanged()
    }

    // Method to remove a task from the list and notify the adapter
    fun removeTask(position: Int) {
        taskList.removeAt(position)
        notifyItemRemoved(position)
    }
}
