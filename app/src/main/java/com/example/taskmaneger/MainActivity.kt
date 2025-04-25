package com.example.taskmaneger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.databinding.ActivityMainBinding
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val listTask = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TaskAdapter(listTask)
        val rvMain: RecyclerView = binding.rvMain
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)

        binding.createButtun.setOnClickListener {
            val taskFormIntent = Intent(this, TaskFormActivity::class.java)
            startActivity(taskFormIntent)
        }
    }

    override fun onResume() {
        super.onResume()
        Thread {
            val app = application as App
            val dao = app.db.taskDao()
            val response = dao.getAll()

            runOnUiThread {
                listTask.clear()
                listTask.addAll(response)
                adapter.notifyDataSetChanged()
                Log.i("TesteDB", "$response")
            }
        }.start()
    }

    private inner class TaskAdapter(
        private val listTask: List<Task>
    ) :
        RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            val view = layoutInflater.inflate(R.layout.main_item, parent, false)
            return TaskViewHolder(view)
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
            val itemCurrent = listTask[position]
            holder.bind(itemCurrent)
        }

        override fun getItemCount(): Int {
            return listTask.size
        }

        private inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Task) {

                val taskNameTxt: TextView = itemView.findViewById(R.id.task_name)
                val descriptionTxt: TextView = itemView.findViewById(R.id.description)
                val priority: View = itemView.findViewById(R.id.priority_level)
                val dateTxt: TextView = itemView.findViewById(R.id.date)

                taskNameTxt.setText(item.taskName)
                descriptionTxt.setText(item.description)
                priority.setBackgroundResource(
                    when (item.priorityLevel) {
                        1 -> R.color.green
                        2 -> R.color.amarelo
                        3 -> R.color.vermelho
                        else -> R.color.white
                    }
                )

                val sdf = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())
                val dateHour = sdf.format(item.dateHour)
                dateTxt.setText(dateHour)
            }
        }
    }
}