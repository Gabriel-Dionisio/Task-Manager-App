package com.example.taskmaneger

data class MainItem(
    val id: Int,
    val taskNameId: String,
    val descriptionId: String,
    val dateHourId: String,
    val doneId: Boolean,
    val priorityLevel: Int
)
