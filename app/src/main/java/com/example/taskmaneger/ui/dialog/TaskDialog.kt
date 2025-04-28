package com.example.taskmaneger

import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaneger.model.Task
import java.text.SimpleDateFormat
import java.util.*

class TaskDialog(
    private val activity: AppCompatActivity,
    private val task: Task? = null,
    private val onSave: (Task) -> Unit
) {

    fun showDialog() {
        val view = LayoutInflater.from(activity).inflate(R.layout.task_dialog, null)

        val title: TextView = view.findViewById(R.id.text_title)
        val editTxtTaskName: EditText = view.findViewById(R.id.edit_task_name)
        val editTxtDescripition: EditText = view.findViewById(R.id.edit_text_descripition)
        val editTxtDate: EditText = view.findViewById(R.id.edit_text_date)
        val editTxtTime: EditText = view.findViewById(R.id.edit_text_time)
        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group_dialog)
        val cancelBtn: Button = view.findViewById(R.id.cancel_button)
        val saveBtn: Button = view.findViewById(R.id.save_button)

        val builder = AlertDialog.Builder(activity).setView(view)

        title.text = if (task == null) "Criar Tarefa" else "Editar Tarefa"

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        task?.let {
            try {
                fillTaskFields(it, editTxtTaskName, editTxtDescripition, editTxtDate, editTxtTime, radioGroup)
            } catch (e: Exception) {
                Toast.makeText(activity, "Erro ao preencher os campos da tarefa", Toast.LENGTH_SHORT).show()
            }
        }

        cancelBtn.setOnClickListener { dialog.dismiss() }

        saveBtn.setOnClickListener {
            try {
                val taskName = editTxtTaskName.text.toString()
                val description = editTxtDescripition.text.toString()

                if (taskName.isBlank() || description.isBlank()) {
                    Toast.makeText(activity, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val finalDateTime = parseDateTime(editTxtDate, editTxtTime)
                val priorityLevel = getPriorityLevel(radioGroup)

                val newTask = Task(
                    taskName = taskName,
                    description = description,
                    dateHour = finalDateTime,
                    done = false,
                    priorityLevel = priorityLevel
                )

                if (task == null) {
                    onSave(newTask)
                    Toast.makeText(activity, "Tarefa salva com sucesso!", Toast.LENGTH_SHORT).show()
                } else {
                    val updatedTask = newTask.copy(id = task.id)
                    onSave(updatedTask)
                    Toast.makeText(activity, "Tarefa atualizada com sucesso!", Toast.LENGTH_SHORT).show()
                }

                dialog.dismiss()
            } catch (e: Exception) {
                Toast.makeText(activity, "Erro ao salvar a tarefa", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun fillTaskFields(
        task: Task,
        taskName: EditText,
        description: EditText,
        date: EditText,
        time: EditText,
        radioGroup: RadioGroup
    ) {
        try {
            taskName.setText(task.taskName)
            description.setText(task.description)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())

            val calendar = Calendar.getInstance()
            calendar.time = task.dateHour

            date.setText(sdf.format(calendar.time))
            time.setText(timeSdf.format(calendar.time))

            val priorityId = when (task.priorityLevel) {
                1 -> R.id.radio_btn_low
                2 -> R.id.radio_btn_medium
                3 -> R.id.radio_btn_high
                else -> R.id.radio_btn_low
            }
            radioGroup.check(priorityId)
        } catch (e: Exception) {
            Toast.makeText(activity, "Erro ao preencher os campos da tarefa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun parseDateTime(dateField: EditText, timeField: EditText): Date {
        return try {
            val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

            val dateOnly = dateFormat.parse(dateField.text.toString().replace("/", ""))!!
            val timeOnly = timeFormat.parse(timeField.text.toString())!!

            Calendar.getInstance().apply {
                time = dateOnly
                set(Calendar.HOUR_OF_DAY, timeOnly.hours)
                set(Calendar.MINUTE, timeOnly.minutes)
            }.time
        } catch (e: Exception) {
            Toast.makeText(activity, "Erro ao parsear a data e hora", Toast.LENGTH_SHORT).show()
            Date()
        }
    }

    private fun getPriorityLevel(radioGroup: RadioGroup): Int {
        return when (radioGroup.checkedRadioButtonId) {
            R.id.radio_btn_low -> 1
            R.id.radio_btn_medium -> 2
            R.id.radio_btn_high -> 3
            else -> 1
        }
    }
}