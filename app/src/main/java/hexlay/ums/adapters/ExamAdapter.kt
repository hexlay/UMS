package hexlay.ums.adapters

import android.annotation.SuppressLint
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.models.Exam
import kotlinx.android.extensions.LayoutContainer

class ExamAdapter(private val exams: List<Exam>) : RecyclerView.Adapter<ExamAdapter.RViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.exam_view, parent, false)
        return RViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RViewHolder, position: Int) {
        viewHolder.bind(exams[position])
    }

    override fun getItemCount(): Int = exams.size

    inner class RViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val examSubject: TextView = containerView.findViewById(R.id.exam_subject)
        private val examHour: TextView = containerView.findViewById(R.id.exam_hour)
        private val examInfo: TextView = containerView.findViewById(R.id.exam_info)

        @SuppressLint("SetTextI18n")
        fun bind(exam: Exam) {
            examSubject.text = exam.subject
            examSubject.isSelected = true
            examHour.text = exam.time
            val examDay = exam.day.replace("-", "/")
            val examHall = if (!TextUtils.isDigitsOnly(exam.hall))
                exam.hall
            else
                "${exam.hall} აუდიტორია"
            examInfo.text = if (exam.seat > 0 )
                "${examDay}, $examHall - ${exam.seat}"
            else
                "${examDay}, $examHall"
        }

    }

}