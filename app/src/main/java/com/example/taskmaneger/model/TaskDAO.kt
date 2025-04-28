package com.example.taskmaneger.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TaskDAO {

    @Insert
    fun insert(task: Task)

    @Query("SELECT * FROM TASK")
    fun getAll(): List<Task>

    @Query("UPDATE task SET done = :isDone WHERE id = :taskId")
    fun updateTaskDoneStatus(taskId: Int, isDone: Boolean)

    @Query("SELECT * FROM task WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Task
}