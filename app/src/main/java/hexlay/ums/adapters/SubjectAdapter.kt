package hexlay.ums.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import hexlay.ums.R
import hexlay.ums.models.subject.Subject
import hexlay.ums.models.subject.SubjectDetail
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_subject_detail_item.view.*

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
        private val subjectHolder: ConstraintLayout = containerView.findViewById(R.id.subject_holder)

        init {
            subjectHolder.setOnClickListener {
                val subjectDetails = subjects[adapterPosition].subjectDetails
                val dialog = MaterialDialog(it.context, BottomSheet()).customView(R.layout.layout_subject_detail)
                val dialogView = dialog.getCustomView()
                val subjectDetailHolder = dialogView.findViewById<LinearLayout>(R.id.subject_detail_holder)
                for (detail in subjectDetails) {
                    subjectDetailHolder.addView(generateTextView(it.context, detail))
                }
                dialog.show {
                    title(text = subjects[adapterPosition].subjectName)
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(subject: Subject) {
            subjectName.text = subject.subjectName
            subjectName.isSelected = true
            subjectTeacher.text = subject.subjectLecturer
            subjectScore.text = subject.subjectScore.toString()
            subjectCredits.text = "საგნის კრედიტი: ${subject.subjectCredit}"
        }

        @SuppressLint("SetTextI18n")
        private fun generateTextView(context: Context, detail: SubjectDetail): View {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_subject_detail_item, null)
            view.subject_detail_name.text = detail.detailName
            view.subject_detail_name.isSelected = true
            if (detail.detailGrade != null) {
                val gradeValue = if (detail.detailType == "group") detail.detailGrade.gradeMax else detail.detailGrade.gradeValue
                view.subject_detail_result.text = "${gradeValue} (${detail.detailMaxScore}-დან)"
            } else {
                view.subject_detail_result.text = "_ (${detail.detailMaxScore}-დან)"
            }
            return view
        }

    }

}