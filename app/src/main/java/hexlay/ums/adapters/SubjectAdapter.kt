package hexlay.ums.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.models.subject.Subject
import kotlinx.android.extensions.LayoutContainer

class SubjectAdapter(var subjects: List<Subject>) : RecyclerView.Adapter<SubjectAdapter.RViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_view_extend, parent, false)
        return RViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RViewHolder, position: Int) {
        viewHolder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class RViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val subjectName: TextView = containerView.findViewById(R.id.subject_name)
        private val subjectTeacher: TextView = containerView.findViewById(R.id.subject_teacher)
        private val subjectCredits: TextView = containerView.findViewById(R.id.subject_credits)
        private val subjectScore: TextView = containerView.findViewById(R.id.subject_score)

        init {
            itemView.setOnClickListener {
                val subject = subjects[adapterPosition]
            }
        }

        fun bind(subject: Subject) {
            subjectName.text = subject.subjectName
            subjectName.isSelected = true
            subjectTeacher.text = subject.subjectLecturer
            subjectScore.text = subject.subjectScore.toString()
            subjectCredits.text = "საგნის კრედიტი: ${subject.subjectCredit}"
        }
    }

}