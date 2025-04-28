import android.content.Context
import android.util.Log
import com.example.taskmaneger.model.App
import com.example.taskmaneger.model.Task
import java.util.concurrent.Executors

class TaskRepository(private val context: Context) {

    private val executor = Executors.newSingleThreadExecutor()

    fun getAllTasks(callback: (List<Task>) -> Unit) {
        executor.execute {
            try {
                val app = context.applicationContext as App
                val dao = app.db.taskDao()
                val allTasks = dao.getAll()
                callback(allTasks)
            } catch (ex: Exception) {
                Log.e("TaskRepository", "Erro ao obter todas as tarefas: ${ex.message}")
            }
        }
    }

    fun saveTask(task: Task, callback: () -> Unit) {
        executor.execute {
            try {
                val app = context.applicationContext as App
                val dao = app.db.taskDao()

                if (task.id == 0) {
                    dao.insert(task)
                } else {
                    dao.updateTask(task.id, task.taskName, task.description, task.dateHour, task.priorityLevel, task.done)
                }
                callback()
            } catch (ex: Exception) {
                Log.e("TaskRepository", "Erro ao salvar a tarefa: ${ex.message}")
            }
        }
    }
}