package com.example.taskmaneger.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.Date

@Dao
interface TaskDAO {

    @Insert
    fun insert(task: Task)

    @Query("SELECT * FROM TASK")
    fun getAll(): List<Task>

    @Query("UPDATE task SET done = :isDone WHERE id = :taskId")
    fun updateTaskDoneStatus(taskId: Int, isDone: Boolean)

    @Query("UPDATE task SET task_name = :taskName, description = :description, date = :dateHour, priority_level = :priorityLevel, done = :done WHERE id = :id")
    fun updateTask(id: Int, taskName: String, description: String, dateHour: Date, priorityLevel: Int, done: Boolean)

    @Query("SELECT * FROM task WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Task
}