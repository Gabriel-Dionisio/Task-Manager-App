package com.example.taskmaneger.ui

import RecyclerItemClickListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

class PendingItemsFragment : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var taskList: MutableList<Task>
    private val executor = Executors.newSingleThreadExecutor()

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return try {
            val binding = FragmentPendingItemsBinding.inflate(inflater, container, false)

            recyclerView = binding.recyclerviewPending
            taskList = mutableListOf()

            taskAdapter = TaskAdapter(requireContext(), taskList, MainActivity.FilterType.PENDING)
            recyclerView.layoutManager = LinearLayoutManager(requireContext())
            recyclerView.adapter = taskAdapter

            val recyclerItemClickListener = RecyclerItemClickListener(requireContext(), recyclerView, object :
                OnItemClickListener {
                override fun onItemClick(view: View, position: Int) {}

                override fun onLongItemClick(view: View, position: Int) {
                    val task = taskList.getOrNull(position)
                    task?.let {
                        openTaskDialog(it)
                    }
                }
            })

            recyclerView.addOnItemTouchListener(recyclerItemClickListener)

            loadTasks()

            binding.root
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao criar a view", e)
            null
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            loadTasks()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao carregar tarefas ao retomar o fragmento", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        executor.shutdown()
    }

    private fun openTaskDialog(task: Task) {
        try {
            val activity = requireActivity() as AppCompatActivity
            val taskDialog = TaskDialog(activity, task) { updatedTask ->
                saveOrUpdateTask(updatedTask)
            }
            taskDialog.showDialog()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao abrir o TaskDialog", e)
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
                loadTasks()
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao salvar ou atualizar a tarefa", e)
            }
        }
    }

    private fun loadTasks() {
        executor.execute {
            try {
                val app = requireActivity().application as App
                val dao = app.db.taskDao()

                val tasks = dao.getAll().filter { !it.done }

                requireActivity().runOnUiThread {
                    taskList.clear()
                    taskList.addAll(tasks)
                    taskAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao carregar tarefas", e)
            }
        }
    }

    fun search(query: String) {
        try {
            taskAdapter.search(query)
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao buscar tarefas", e)
        }
    }

    fun clearSearch() {
        try {
            taskAdapter.clearSearch()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao limpar busca", e)
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
                Log.e(TAG, "Erro ao atualizar status da tarefa", e)
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val TAG = "PendingItemsFragment"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PendingItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}