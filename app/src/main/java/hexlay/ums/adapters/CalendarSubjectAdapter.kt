package hexlay.ums.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.fragments.CalendarFragment
import hexlay.ums.models.subject.Subject
import kotlinx.android.extensions.LayoutContainer

class CalendarSubjectAdapter(val calendarFragment: CalendarFragment) : RecyclerView.Adapter<CalendarSubjectAdapter.RViewHolder>() {

    val subjects = mutableListOf<Subject>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_view, parent, false)
        return RViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RViewHolder, position: Int) {
        viewHolder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class RViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val subjectText: TextView = containerView.findViewById(R.id.subject_name)

        init {
            itemView.setOnClickListener {
                val subject = subjects[adapterPosition]
            }
        }

        fun bind(subject: Subject) {
            subjectText.text = subject.subjectName
        }
    }

}