package com.example.taskmaneger

import TaskRepository
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.databinding.ActivityMainBinding
import com.example.taskmaneger.model.Task
import com.example.taskmaneger.taskmaneger.Adapter.ViewPagerAdapter
import com.example.taskmaneger.taskmaneger.Adapter.manager.TaskManager
import com.ferfalk.simplesearchview.SimpleSearchView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private lateinit var taskRepository: TaskRepository
    private lateinit var taskManager: TaskManager
    private val listTask = mutableListOf<Task>()

    private var isFiltering = false
    private var isSearching = false

    enum class FilterType { PENDING, COMPLETED }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            enableEdgeToEdge()
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            setSupportActionBar(binding.toolbar)

            taskRepository = TaskRepository(applicationContext)
            taskManager = TaskManager()

            setupRecyclerView()
            setupViewPager()
            setupListeners()
            initSearchView()
            loadTasks()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupRecyclerView() {
        try {
            adapter = TaskAdapter(this, listTask, FilterType.PENDING)
            recyclerView = binding.rvMain
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = adapter
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupViewPager() {
        try {
            val viewPagerAdapter = ViewPagerAdapter(this)
            binding.viewPager.adapter = viewPagerAdapter

            TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
                tab.text = if (position == 0) "Pendentes" else "Concluídas"
            }.attach()

            binding.tabLayout.elevation = 0f
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupListeners() {
        try {
            binding.createButton.setOnClickListener {
                openTaskDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initSearchView() {
        try {
            binding.searchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
                override fun onQueryTextChange(query: String): Boolean {
                    return try {
                        isSearching = query.isNotEmpty()
                        if (isSearching) showSearchResults(query) else hideSearchResults()
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }

                override fun onQueryTextCleared(): Boolean {
                    return try {
                        isSearching = false
                        hideSearchResults()
                        true
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                }

                override fun onQueryTextSubmit(query: String): Boolean = false
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showSearchResults(query: String) {
        try {
            recyclerView.visibility = View.VISIBLE
            binding.viewPager.visibility = View.GONE
            searchTasks(query)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideSearchResults() {
        try {
            if (!isFiltering) {
                recyclerView.visibility = View.GONE
                binding.viewPager.visibility = View.VISIBLE
                listTask.clear()
                adapter.notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun searchTasks(query: String?) {
        try {
            taskRepository.getAllTasks { tasks ->
                try {
                    val filteredTasks = taskManager.searchTasks(tasks, query ?: "")
                    runOnUiThread {
                        listTask.clear()
                        listTask.addAll(filteredTasks)
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFilterBottomSheet() {
        try {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_filter, null)
            val dialog = BottomSheetDialog(this)
            dialog.setContentView(bottomSheetView)

            val chipPriorityHigh: Chip = bottomSheetView.findViewById(R.id.chipPriorityHigh)
            val chipPriorityMedium: Chip = bottomSheetView.findViewById(R.id.chipPriorityMedium)
            val chipPriorityLow: Chip = bottomSheetView.findViewById(R.id.chipPriorityLow)
            val clearButton: Button = bottomSheetView.findViewById(R.id.btnClearFilter)

            chipPriorityHigh.setOnClickListener { applyFilterAndCloseDialog(3, dialog) }
            chipPriorityMedium.setOnClickListener { applyFilterAndCloseDialog(2, dialog) }
            chipPriorityLow.setOnClickListener { applyFilterAndCloseDialog(1, dialog) }
            clearButton.setOnClickListener {
                hideFilterResults()
                dialog.dismiss()
            }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun applyFilterAndCloseDialog(priority: Int, dialog: BottomSheetDialog) {
        try {
            isFiltering = true
            showFilterResults(priority)
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showFilterResults(priority: Int) {
        try {
            recyclerView.visibility = View.VISIBLE
            binding.viewPager.visibility = View.GONE
            filterByPriority(priority)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideFilterResults() {
        try {
            recyclerView.visibility = View.GONE
            binding.viewPager.visibility = View.VISIBLE
            listTask.clear()
            adapter.notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun filterByPriority(priority: Int) {
        try {
            taskRepository.getAllTasks { tasks ->
                try {
                    val filteredTasks = taskManager.filterByPriority(tasks, priority)
                    runOnUiThread {
                        listTask.clear()
                        listTask.addAll(filteredTasks)
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openTaskDialog(task: Task? = null) {
        try {
            TaskDialog(this, task) { savedTask ->
                saveTask(savedTask)
            }.showDialog()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveTask(task: Task) {
        try {
            taskRepository.saveTask(task) {
                loadTasks()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadTasks() {
        try {
            taskRepository.getAllTasks { tasks ->
                try {
                    val tasksNotDone = tasks.filter { !it.done }
                    runOnUiThread {
                        listTask.clear()
                        listTask.addAll(tasksNotDone)
                        adapter.notifyDataSetChanged()

                        if (!isFiltering && !isSearching) {
                            recyclerView.visibility = View.GONE
                            binding.viewPager.visibility = View.VISIBLE
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return try {
            menuInflater.inflate(R.menu.menu_main, menu)
            binding.searchView.setMenuItem(menu!!.findItem(R.id.menu_search))
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return try {
            when (item.itemId) {
                R.id.menu_search -> true
                R.id.menu_filter -> {
                    showFilterBottomSheet()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
