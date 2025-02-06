package com.example.taskmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.taskmanager.R
import com.example.taskmanager.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignupActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignupBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Navigate to LoginActivity
        binding.btnSignupnavigate.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Signup button click listener
        binding.signupButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.newPassword.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            // Validate email and passwords
            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                // Create user with email and password
                createUser(email, password)
            }
        }

        // Handle insets for edge-to-edge UI
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // User registration successful
                    val user = auth.currentUser

                    // Save user data to Firebase Realtime Database
                    val userId = user?.uid
                    val database = FirebaseDatabase.getInstance().getReference("Users")
                    val userMap = mapOf(
                        "email" to email,
                        "uid" to userId
                    )

                    userId?.let {
                        database.child(it).setValue(userMap)
                            .addOnSuccessListener {
                                // User data saved successfully
                                Toast.makeText(this, "Signup successful", Toast.LENGTH_SHORT).show()
                                // Navigate to the next screen (LoginActivity)
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                // Handle errors
                                Toast.makeText(this, "Failed to save user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // If sign-up fails, display an error message
                    Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
