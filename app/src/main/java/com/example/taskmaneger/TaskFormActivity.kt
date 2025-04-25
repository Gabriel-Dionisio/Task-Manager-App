package com.example.taskmaneger

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.taskmaneger.databinding.ActivityTaskFormBinding
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTaskFormBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityTaskFormBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.saveButton.setOnClickListener {
            Thread {
                val taskName = binding.editTaskName.text.toString()
                val description = binding.editDescription.text.toString()

                val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                // Converte texto para Date
                val dateOnly = dateFormat.parse(binding.editTextDate.text.toString())!!
                val timeOnly = timeFormat.parse(binding.editTextTime.text.toString())!!

                // Junta data e hora num único Date
                val calendarDate = Calendar.getInstance()
                calendarDate.time = dateOnly

                val calendarTime = Calendar.getInstance()
                calendarTime.time = timeOnly

                calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
                calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))

                val finalDateTime = calendarDate.time

                val selectedId = binding.radioGroup.checkedRadioButtonId
                val priorityLevel = when (selectedId) {
                    R.id.low_button -> 1
                    R.id.medium_button -> 2
                    R.id.high_button -> 3
                    else -> 0 // padrão ou erro
                }

                val app = application as App
                val dao = app.db.taskDao()

                // Inserindo no banco de dados na thread de background
                dao.insert(
                    Task(
                        taskName = taskName,
                        description = description,
                        dateHour = finalDateTime,
                        done = false,
                        priorityLevel = priorityLevel
                    )
                )

                // Atualizando a UI na thread principal
                runOnUiThread {
                    Toast.makeText(
                        this@TaskFormActivity,
                        "Tarefa salva com sucesso!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.start()  // Inicia a thread
        }
    }
}