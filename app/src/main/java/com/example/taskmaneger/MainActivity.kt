package com.example.taskmaneger

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
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
import kotlin.concurrent.thread

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
        val seachView = binding.searchTask
        seachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                Thread {
                    val app = application as App
                    val dao = app.db.taskDao()
                    val allTasks = dao.getAll()
                    val filteredTask = if (p0.isNullOrEmpty()) {
                        allTasks.filter { !it.done }
                    } else {
                        allTasks.filter { it.taskName.contains(p0, true) }
                    }

                    runOnUiThread {
                        listTask.clear()
                        listTask.addAll(filteredTask)
                        adapter.notifyDataSetChanged()
                        binding.filterLabel.setText("Encontrados:")
                        binding.filterResult.setText(filteredTask.size.toString())
                    }
                }.start()
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

        seachView.setOnCloseListener(object : SearchView.OnCloseListener{
            override fun onClose(): Boolean {
                loadTask()
                return false
            }
        })

        binding.filter.setOnClickListener {
            if (binding.taskFilterComponent.visibility == View.VISIBLE) {
                binding.taskFilterComponent.visibility = View.GONE
            } else {
                binding.taskFilterComponent.visibility = View.VISIBLE
            }
        }

        binding.chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
            Thread {
                val app = application as App
                val dao = app.db.taskDao()
                val allTasks = dao.getAll()

                var filteredTask = when {
                    checkedIds.contains(R.id.chipPending) -> allTasks.filter { !it.done }
                    checkedIds.contains(R.id.chipCompleted) -> allTasks.filter { it.done }
                    else -> allTasks.filter { !it.done }
                }

                runOnUiThread {
                    listTask.clear()
                    listTask.addAll(filteredTask)
                    adapter.notifyDataSetChanged()
                    binding.filterLabel.setText("Encontrados:")
                    binding.filterResult.setText(filteredTask.size.toString())
                }
            }.start()
        }

        binding.chipGroupPriority.setOnCheckedStateChangeListener { group, checkedIds ->
            thread {
                val app = application as App
                val dao = app.db.taskDao()
                val allTasks = dao.getAll()

                var filteredTask = when {
                    checkedIds.contains(R.id.chipLow) -> allTasks.filter { it.priorityLevel == 1 }
                    checkedIds.contains(R.id.chipMedium) -> allTasks.filter { it.priorityLevel == 2 }
                    checkedIds.contains(R.id.chipHigh) -> allTasks.filter { it.priorityLevel == 3 }
                    else -> allTasks.filter { !it.done }
                }

                runOnUiThread {
                    listTask.clear()
                    listTask.addAll(filteredTask)
                    adapter.notifyDataSetChanged()
                    binding.filterLabel.setText("Encontrados:")
                    binding.filterResult.setText(filteredTask.size.toString())
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        loadTask()
    }

    fun loadTask() {
        Thread {
            val app = application as App
            val dao = app.db.taskDao()
            val allTasks = dao.getAll()

            val tasksNotDone = allTasks.filter { !it.done }

            runOnUiThread {
                listTask.clear()
                listTask.addAll(tasksNotDone)
                adapter.notifyDataSetChanged()
                Log.i("TesteDB", "$tasksNotDone")
            }
        }.start()
    }

    private inner class TaskAdapter(
        private var listTask: List<Task>
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