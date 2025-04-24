package com.example.taskmaneger

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainItens = mutableListOf<MainItem>()
        mainItens.add(
            MainItem(
                1,
                "Lavar Roupa",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
                "24/04/2025 - 13:00",
                false,
                R.color.green
            )
        )
        mainItens.add(
            MainItem(
                1,
                "Estudar",
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. ",
                "24/04/2025 - 14:00",
                true,
                R.color.amarelo
            )
        )


        val adapter = MainAdapter(mainItens) {it ->
            Toast.makeText(this, "Teste $it", Toast.LENGTH_SHORT).show()
        }
        var rvMain: RecyclerView = binding.rvMain
        rvMain.adapter = adapter
        rvMain.layoutManager = LinearLayoutManager(this)
    }

    private inner class MainAdapter(
        private val mainItens: List<MainItem>,
        private val onItemClickListener: (String) -> Unit
    ) :
        RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            val view = layoutInflater.inflate(R.layout.main_item, parent, false)
            return MainViewHolder(view)
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val itemCurrent = mainItens[position]
            holder.bind(itemCurrent)
        }

        override fun getItemCount(): Int {
            return mainItens.size
        }

        private inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: MainItem) {
                val container: ConstraintLayout = itemView.findViewById(R.id.container)
                val taskName: TextView = itemView.findViewById(R.id.task_name)
                val descripition: TextView = itemView.findViewById(R.id.description)
                val dateHour: TextView = itemView.findViewById(R.id.date_hour)
                val checkBox: CheckBox = itemView.findViewById(R.id.check_box)
                val priorityLevel: View = itemView.findViewById(R.id.priority_level)

                taskName.setText(item.taskNameId)
                descripition.setText(item.descriptionId)
                dateHour.setText(item.dateHourId)
                checkBox.isChecked = item.doneId
                priorityLevel.setBackgroundResource(item.priorityLevel)

                container.setOnClickListener{
                    onItemClickListener.invoke(item.taskNameId)
                }
            }
        }
    }
}