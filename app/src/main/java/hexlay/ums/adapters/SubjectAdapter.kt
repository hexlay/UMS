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
import hexlay.ums.helpers.canBeInt
import hexlay.ums.models.subject.Subject
import hexlay.ums.models.subject.SubjectDetail
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.layout_subject_detail_item.view.*

class SubjectAdapter(private var subjects: List<Subject>) : RecyclerView.Adapter<SubjectAdapter.RViewHolder>() {

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
                val subjectDetails = subjects[adapterPosition].details
                if (subjectDetails != null && subjectDetails.isNotEmpty()) {
                    val dialog = MaterialDialog(it.context, BottomSheet()).customView(R.layout.layout_subject_detail)
                    val dialogView = dialog.getCustomView()
                    val subjectDetailHolder = dialogView.findViewById<LinearLayout>(R.id.subject_detail_holder)
                    for (detail in subjectDetails) {
                        subjectDetailHolder.addView(generateTextView(it.context, detail))
                    }
                    dialog.show {
                        title(text = subjects[adapterPosition].name)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(subject: Subject) {
            subjectName.text = subject.name
            subjectName.isSelected = true
            subjectTeacher.text = subject.lecturer
            subjectScore.text = if (subject.score > 0) {
                if (subject.score.canBeInt())
                    subject.score.toInt().toString()
                else
                    subject.score.toString()
            } else if(subject.fullScore > 0) {
                if (subject.fullScore.canBeInt())
                    "0 (${subject.fullScore.toInt()})"
                else
                    "0 (${subject.fullScore})"
            } else {
                "0"
            }
            subjectCredits.text = "კრედიტი: ${subject.credit}"
        }

        @Suppress("IMPLICIT_CAST_TO_ANY")
        @SuppressLint("SetTextI18n")
        private fun generateTextView(context: Context, detail: SubjectDetail): View {
            val view = LayoutInflater.from(context).inflate(R.layout.layout_subject_detail_item, null)
            view.subject_detail_name.text = detail.name
            view.subject_detail_name.isSelected = true
            if (detail.grade != null) {
                val gradeValue = if (detail.type == "group")
                    if (detail.grade.maximumScore.canBeInt())
                        detail.grade.maximumScore.toInt()
                    else
                        detail.grade.maximumScore
                else
                    if (detail.grade.relativeScore.canBeInt())
                        detail.grade.relativeScore.toInt()
                    else
                        detail.grade.relativeScore
                view.subject_detail_result.text = "$gradeValue (${detail.maximalScore.toInt()}-დან)"
            } else {
                view.subject_detail_result.text = "_ (${detail.maximalScore.toInt()}-დან)"
            }
            return view
        }

    }

}