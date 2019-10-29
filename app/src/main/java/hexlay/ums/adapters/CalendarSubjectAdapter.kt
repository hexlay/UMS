package hexlay.ums.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.helpers.AppHelper
import hexlay.ums.models.session.Session
import kotlinx.android.extensions.LayoutContainer
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CalendarSubjectAdapter : RecyclerView.Adapter<CalendarSubjectAdapter.RViewHolder>() {

    private val subjects = ArrayList<Session>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.subject_view_calendar, parent, false)
        return RViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: RViewHolder, position: Int) {
        viewHolder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    inner class RViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val subjectText: TextView = containerView.findViewById(R.id.subject_name)
        private val subjectType: TextView = containerView.findViewById(R.id.subject_type)
        private val subjectPlace: TextView = containerView.findViewById(R.id.subject_place)
        private val subjectTime: TextView = containerView.findViewById(R.id.subject_time)

        @SuppressLint("SetTextI18n")
        fun bind(subject: Session) {
            subjectText.text = subject.sessionGroup.subjectActivation.subject.name
            subjectText.isSelected = true
            subjectType.text = if (subject.sessionGroup.sessionType == "lecture") "ლექცია" else "სემინარი"
            val building = if (subject.room.building == "building01") "I კორპუსი" else "II კორპუსი"
            subjectPlace.text = "$building ${subject.room.name}"
            setTime(subject.startTime)
        }

        private fun setTime(time: String) {
            if (AppHelper.isPie) {
                val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.getDefault())
                val outputFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
                subjectTime.text = outputFormatter.format(inputFormatter.parse(time)!!.time)
            } else {
                subjectTime.text = "TBA"
            }
        }

    }

    fun changeSubjects(newData: List<Session>) {
        subjects.clear()
        subjects.addAll(newData)
        notifyDataSetChanged()
    }

}