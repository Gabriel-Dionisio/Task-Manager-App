package com.example.taskmaneger.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "task_name") val taskName: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "date") val dateHour: Date,
    @ColumnInfo(name = "done") val done: Boolean,
    @ColumnInfo(name = "priority_level") val priorityLevel: Int
)
