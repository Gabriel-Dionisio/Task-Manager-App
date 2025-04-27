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
}