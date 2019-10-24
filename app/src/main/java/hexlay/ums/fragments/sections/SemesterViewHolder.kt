package hexlay.ums.fragments.sections

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R


class SemesterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    var subjectName: TextView = itemView.findViewById(R.id.subject_name)
    var subjectTeacher: TextView = itemView.findViewById(R.id.subject_teacher)
    var subjectScore: TextView = itemView.findViewById(R.id.subject_score)
    var subjectCredit: TextView = itemView.findViewById(R.id.subject_credits)

}