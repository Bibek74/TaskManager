package com.example.taskmanager.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.taskmanager.R
import com.example.taskmanager.model.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AddFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Initialize Firebase
        database = FirebaseDatabase.getInstance().getReference("Tasks")
        auth = FirebaseAuth.getInstance()

        val titleInput = view.findViewById<EditText>(R.id.title_input)
        val descriptionInput = view.findViewById<EditText>(R.id.description_input)
        val startDateInput = view.findViewById<EditText>(R.id.start_date_input)
        val endDateInput = view.findViewById<EditText>(R.id.end_date_input)
        val addButton = view.findViewById<Button>(R.id.add_task_button)

        addButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val description = descriptionInput.text.toString().trim()
            val startDate = startDateInput.text.toString().trim()
            val endDate = endDateInput.text.toString().trim()
            val userId = auth.currentUser?.uid ?: ""

            if (title.isNotEmpty() && description.isNotEmpty() && startDate.isNotEmpty() && endDate.isNotEmpty()) {
                val taskId = database.push().key ?: return@setOnClickListener
                val task = Task(title, description, startDate, endDate, userId)

                database.child(taskId).setValue(task)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Task added successfully", Toast.LENGTH_SHORT).show()
                        clearFields(titleInput, descriptionInput, startDateInput, endDateInput)
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), "Failed to add task", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun clearFields(vararg inputs: EditText) {
        for (input in inputs) {
            input.text.clear()
        }
    }

    private fun getCalendarDates(): List<String> {
        // Generate a list of dates for the calendar
        return listOf("Sat 18", "Sun 19", "Mon 20", "Tue 21", "Wed 22")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AddFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
