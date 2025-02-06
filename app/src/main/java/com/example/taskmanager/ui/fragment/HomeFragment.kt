package com.example.taskmanager.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.model.Task
import com.example.taskmanager.adapter.TaskAdapter
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var emptyStateMessage: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.task_list_recycler_view)
        emptyStateMessage = view.findViewById(R.id.empty_state_message)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(mutableListOf(), requireContext(), ::onUpdateClick, ::onDeleteClick)
        recyclerView.adapter = taskAdapter

        database = FirebaseDatabase.getInstance().getReference("Tasks")
        fetchTasks()

        return view
    }

    private fun fetchTasks() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val taskList = mutableListOf<Task>()
                for (taskSnapshot in snapshot.children) {
                    val task = taskSnapshot.getValue(Task::class.java)
                    task?.let {
                        taskList.add(it.copy(id = taskSnapshot.key ?: ""))
                    }
                }
                taskAdapter.updateTasks(taskList)
                emptyStateMessage.visibility = if (taskList.isEmpty()) View.VISIBLE else View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Update task function to handle updates based on updated data passed from TaskAdapter
    private fun onUpdateClick(task: Task, updatedTitle: String, updatedDescription: String, updatedStartDate: String, updatedEndDate: String) {
        // Update task in Firebase with the new values
        val taskRef = database.child(task.id) // Firebase key is used here
        val updatedTask = task.copy(
            title = updatedTitle,
            description = updatedDescription,
            startDate = updatedStartDate,
            endDate = updatedEndDate
        )

        taskRef.setValue(updatedTask).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Task updated successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to update task.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Delete task function
    private fun onDeleteClick(task: Task) {
        // Handle delete logic
        Toast.makeText(requireContext(), "Deleting task: ${task.title}", Toast.LENGTH_SHORT).show()

        // Delete task from Firebase
        val taskRef = database.child(task.id) // Firebase key is used here
        taskRef.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(requireContext(), "Task deleted successfully!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Failed to delete task.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
