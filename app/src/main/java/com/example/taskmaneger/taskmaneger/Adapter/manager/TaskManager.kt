package com.example.taskmaneger.taskmaneger.Adapter.manager

import android.util.Log
import com.example.taskmaneger.model.Task

class TaskManager {

    fun filterByPriority(tasks: List<Task>, priority: Int): List<Task> {
        return try {
            tasks.filter { it.priorityLevel == priority && !it.done }
        } catch (e: Exception) {
            Log.e("TaskManager", "Erro ao filtrar tarefas por prioridade: ${e.message}")
            emptyList()
        }
    }

    fun searchTasks(tasks: List<Task>, query: String): List<Task> {
        return try {
            tasks.filter { it.taskName.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            Log.e("TaskManager", "Erro ao buscar tarefas: ${e.message}")
            emptyList()
        }
    }
}