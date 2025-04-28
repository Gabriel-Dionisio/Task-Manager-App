package com.example.taskmaneger

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class TaskAdapter(
    private val context: Context,
    private var listTask: MutableList<Task>,
    private val currentFilter: MainActivity.FilterType
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val executor = Executors.newSingleThreadExecutor()
    private var list = listTask

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.main_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val itemCurrent = list[position]
        holder.bind(itemCurrent)
    }

    fun search(query: String): Boolean {
        try {
            list.clear()
            list.addAll(listTask.filter { it.taskName.contains(query, true) })
            notifyDataSetChanged()

            return list.isEmpty()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao realizar a busca: ${e.message}", Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun clearSearch() {
        try {
            list = listTask.toMutableList()
            notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao limpar a busca: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateList(newList: List<Task>) {
        try {
            list.clear()
            list.addAll(newList)
            notifyDataSetChanged()
        } catch (e: Exception) {
            Toast.makeText(context, "Erro ao atualizar a lista: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = list.size

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val taskNameTxt: TextView = itemView.findViewById(R.id.task_name)
        private val descriptionTxt: TextView = itemView.findViewById(R.id.description)
        private val priority: View = itemView.findViewById(R.id.priority_level)
        private val dateTxt: TextView = itemView.findViewById(R.id.date)
        private val checkBox: CheckBox = itemView.findViewById(R.id.check_box)

        fun bind(item: Task) {
            try {
                taskNameTxt.text = item.taskName
                descriptionTxt.text = item.description

                priority.setBackgroundResource(
                    when (item.priorityLevel) {
                        1 -> R.color.priority_low
                        2 -> R.color.priority_medium
                        3 -> R.color.priority_high
                        else -> R.color.white
                    }
                )

                val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                dateTxt.text = sdf.format(item.dateHour)

                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = item.done

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    updateTaskDoneStatus(item, isChecked)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Erro ao preencher os dados da tarefa: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        private fun updateTaskDoneStatus(task: Task, isChecked: Boolean) {
            executor.execute {
                try {
                    val app = context.applicationContext as App
                    val dao = app.db.taskDao()

                    dao.updateTaskDoneStatus(task.id, isChecked)

                    (context as? Activity)?.runOnUiThread {
                        val position = listTask.indexOf(task)
                        if (position != -1) {
                            val updatedItem = task.copy(done = isChecked)
                            listTask[position] = updatedItem

                            if (shouldRemoveItem(isChecked)) {
                                listTask.removeAt(position)
                                notifyItemRemoved(position)
                            } else {
                                notifyItemChanged(position)
                            }
                        }
                    }
                } catch (e: Exception) {
                    (context as? Activity)?.runOnUiThread {
                        Toast.makeText(context, "Erro ao atualizar o status da tarefa: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        private fun shouldRemoveItem(isChecked: Boolean): Boolean {
            return when (currentFilter) {
                MainActivity.FilterType.PENDING -> isChecked
                MainActivity.FilterType.COMPLETED -> !isChecked
            }
        }
    }
}
