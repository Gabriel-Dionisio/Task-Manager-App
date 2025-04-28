package com.example.taskmaneger

import RecyclerItemClickListener
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.databinding.ActivityMainBinding
import com.example.taskmaneger.listener.OnItemClickListener
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val listTask = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter
    private val executor = Executors.newSingleThreadExecutor()

    private var currentFilter: FilterType = FilterType.PENDING

    enum class FilterType {
        ALL, PENDING, COMPLETED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TaskAdapter(listTask)
        val rvMain: RecyclerView = binding.rvMain
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)

        val recyclerItemClickListener = RecyclerItemClickListener(this, rvMain, object :
            OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
            }

            override fun onLongItemClick(view: View, position: Int) {
                val task = listTask[position]
                showEditTaskDialog(task)
            }
        })

        rvMain.addOnItemTouchListener(recyclerItemClickListener)

        binding.createButtun.setOnClickListener {
            showEditTaskDialog()
        }
        val seachView = binding.searchTask
        seachView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(p0: String?): Boolean {
                executor.execute {
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
                }
                return true
            }

            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }
        })

        seachView.setOnCloseListener(object : SearchView.OnCloseListener {
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
            executor.execute {
                val app = application as App
                val dao = app.db.taskDao()
                val allTasks = dao.getAll()

                val filteredTask = when {
                    checkedIds.contains(R.id.chipPending) -> allTasks.filter { !it.done }
                    checkedIds.contains(R.id.chipCompleted) -> allTasks.filter { it.done }
                    checkedIds.contains(R.id.chipAll) -> allTasks
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

        binding.chipGroupPriority.setOnCheckedStateChangeListener { group, checkedIds ->
            executor.execute {
                val app = application as App
                val dao = app.db.taskDao()
                val allTasks = dao.getAll()

                val filteredTask = when {
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

    private fun showEditTaskDialog(task: Task? = null) {
        val dialog = Dialog(this@MainActivity)
        val view = layoutInflater.inflate(R.layout.task_dialog, null)
        val editTxtTaskName: EditText = view.findViewById(R.id.edit_task_name)
        val editTxtDescripition: EditText = view.findViewById(R.id.edit_text_descripition)
        val editTxtDate: EditText = view.findViewById(R.id.edit_text_date)
        val editTxtTime: EditText = view.findViewById(R.id.edit_text_time)
        val radioGroup: RadioGroup = view.findViewById(R.id.radio_group_dialog)
        val cancelBtn: Button = view.findViewById(R.id.cancel_button)
        val saveBtn: Button = view.findViewById(R.id.save_button)

        if (task != null) {
            editTxtTaskName.setText(task.taskName)
            editTxtDescripition.setText(task.description)

            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val timeSdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = task.dateHour
            editTxtDate.setText(sdf.format(calendar.time))
            editTxtTime.setText(timeSdf.format(calendar.time))

            when (task.priorityLevel) {
                1 -> radioGroup.check(R.id.radio_btn_low)
                2 -> radioGroup.check(R.id.radio_btn_medium)
                3 -> radioGroup.check(R.id.radio_btn_high)
            }
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        saveBtn.setOnClickListener {
            Thread {
                val taskName = editTxtTaskName.text.toString()
                val description = editTxtDescripition.text.toString()

                val dateFormat = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

                val dateOnly = dateFormat.parse(editTxtDate.text.toString().replace("/", ""))!!
                val timeOnly = timeFormat.parse(editTxtTime.text.toString())!!

                val calendarDate = Calendar.getInstance()
                calendarDate.time = dateOnly

                val calendarTime = Calendar.getInstance()
                calendarTime.time = timeOnly

                calendarDate.set(Calendar.HOUR_OF_DAY, calendarTime.get(Calendar.HOUR_OF_DAY))
                calendarDate.set(Calendar.MINUTE, calendarTime.get(Calendar.MINUTE))

                val finalDateTime = calendarDate.time

                val selectedId = radioGroup.checkedRadioButtonId
                val priorityLevel = when (selectedId) {
                    R.id.radio_btn_low -> 1
                    R.id.radio_btn_medium -> 2
                    R.id.radio_btn_high -> 3
                    else -> 1
                }

                val app = application as App
                val dao = app.db.taskDao()

                if (task == null) {
                    dao.insert(
                        Task(
                            taskName = taskName,
                            description = description,
                            dateHour = finalDateTime,
                            done = false,
                            priorityLevel = priorityLevel
                        )
                    )
                } else {
                    dao.updateTask(
                        id = task.id,
                        taskName = taskName,
                        description = description,
                        dateHour = finalDateTime,
                        done = false,
                        priorityLevel = priorityLevel
                    )
                }

                runOnUiThread {
                    if (task == null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Tarefa salva com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Tarefa atualizada com sucesso!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    loadTask()
                    dialog.dismiss()
                }
            }.start()
        }

        dialog.setContentView(view)
        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        loadTask()
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    fun loadTask() {
        executor.execute {
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
        }
    }

    private inner class TaskAdapter(
        private var listTask: MutableList<Task>
    ) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

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
                val checkBox: CheckBox = itemView.findViewById(R.id.check_box)

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

                checkBox.setOnCheckedChangeListener(null)
                checkBox.isChecked = item.done

                checkBox.setOnCheckedChangeListener { _, isChecked ->
                    executor.execute {
                        val app = application as App
                        val dao = app.db.taskDao()

                        dao.updateTaskDoneStatus(item.id, isChecked)

                        runOnUiThread {
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val updatedItem = item.copy(done = isChecked)

                                listTask[position] = updatedItem

                                val shouldRemove = when (currentFilter) {
                                    FilterType.PENDING -> isChecked
                                    FilterType.COMPLETED -> !isChecked
                                    FilterType.ALL -> false
                                }

                                if (shouldRemove) {
                                    listTask.removeAt(position)
                                    notifyItemRemoved(position)
                                } else {
                                    notifyItemChanged(position)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
