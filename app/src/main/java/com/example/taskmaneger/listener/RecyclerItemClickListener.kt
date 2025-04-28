import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmaneger.listener.OnItemClickListener

class RecyclerItemClickListener(
    context: Context,
    recyclerView: RecyclerView,
    private val mListener: OnItemClickListener?
) : RecyclerView.OnItemTouchListener {

    private val mGestureDetector: GestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            val child = recyclerView.findChildViewUnder(e.x, e.y)
            child?.let {
                mListener?.onItemClick(it, recyclerView.getChildAdapterPosition(it))
            }
            return false
        }

        override fun onLongPress(e: MotionEvent) {
            val child = recyclerView.findChildViewUnder(e.x, e.y)
            child?.let {
                mListener?.onLongItemClick(it, recyclerView.getChildAdapterPosition(it))
            }
        }
    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val childView = rv.findChildViewUnder(e.x, e.y)
        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            return true
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
}