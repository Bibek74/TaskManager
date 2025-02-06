package com.example.taskmanager.model

data class Task(
    val id: String = "", // Add this ID field to uniquely identify each task
    val title: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val userId: String = ""
)
