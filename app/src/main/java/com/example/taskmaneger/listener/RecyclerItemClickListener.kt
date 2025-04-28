import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.listener.OnItemClickListener

class RecyclerItemClickListener(
    context: Context,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) : RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            return try {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                child?.let {
                    mListener?.onItemClick(it, recyclerView.getChildAdapterPosition(it))
                }
                true
            } catch (ex: Exception) {
                Log.e("RecyclerItemClickListener", "Erro ao processar o clique: ${ex.message}")
                false
            }
        }

        override fun onLongPress(e: MotionEvent) {
            try {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                child?.let {
                    mListener?.onLongItemClick(it, recyclerView.getChildAdapterPosition(it))
                }
            } catch (ex: Exception) {
                Log.e("RecyclerItemClickListener", "Erro ao processar o longo clique: ${ex.message}")
            }
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        return try {
            val childView = rv.findChildViewUnder(e.x, e.y)
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                true
            } else {
                false
            }
        } catch (ex: Exception) {
            Log.e("RecyclerItemClickListener", "Erro ao interceptar evento de toque: ${ex.message}")
            false
        }
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}