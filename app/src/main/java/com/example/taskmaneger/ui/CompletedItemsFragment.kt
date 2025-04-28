package com.example.taskmaneger.ui

import RecyclerItemClickListener
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.MainActivity
import com.example.taskmaneger.TaskAdapter
import com.example.taskmaneger.TaskDialog
import com.example.taskmaneger.databinding.FragmentPendingItemsBinding
import com.example.taskmaneger.listener.OnItemClickListener
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.util.concurrent.Executors

class CompletedItemsFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskList: MutableList<Task>
    private val executor = Executors.newSingleThreadExecutor()

    private var _binding: FragmentPendingItemsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            _binding = FragmentPendingItemsBinding.inflate(inflater, container, false)

            recyclerView = binding.recyclerviewPending
            taskList = mutableListOf()

            taskAdapter = TaskAdapter(requireContext(), taskList, MainActivity.FilterType.COMPLETED)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = taskAdapter

            val recyclerItemClickListener = RecyclerItemClickListener(requireContext(), recyclerView, object :
                OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {}

                override fun onLongItemClick(view: View, position: Int) {
                    if (position in taskList.indices) {
                        val task = taskList[position]
                        openTaskDialog(task)
                    }
                }
            })

            recyclerView.addOnItemTouchListener(recyclerItemClickListener)

            loadTasks()

            binding.root
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            loadTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun openTaskDialog(task: Task) {
        try {
            val taskDialog = TaskDialog(requireActivity() as AppCompatActivity, task) { updatedTask ->
                saveOrUpdateTask(updatedTask)
            }
            taskDialog.showDialog()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveOrUpdateTask(task: Task) {
        executor.execute {
            try {
                val app = requireActivity().application as App
                val dao = app.db.taskDao()

                if (task.id == null) {
                    dao.insert(task)
                } else {
                    dao.updateTask(
                        id = task.id,
                        taskName = task.taskName,
                        description = task.description,
                        dateHour = task.dateHour,
                        priorityLevel = task.priorityLevel,
                        done = task.done
                    )
                }

                requireActivity().runOnUiThread {
                    loadTasks()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun loadTasks() {
        executor.execute {
            try {
                val app = requireActivity().application as App
                val dao = app.db.taskDao()

                val tasks = dao.getAll().filter { it.done }

                requireActivity().runOnUiThread {
                    taskList.clear()
                    taskList.addAll(tasks)
                    taskAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun search(query: String) {
        try {
            taskAdapter.search(query)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearSearch() {
        try {
            taskAdapter.clearSearch()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateTaskDoneStatus(task: Task) {
        executor.execute {
            try {
                val app = requireActivity().application as App
                val dao = app.db.taskDao()

                dao.updateTaskDoneStatus(task.id, task.done)
                loadTasks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = CompletedItemsFragment()
    }
}
