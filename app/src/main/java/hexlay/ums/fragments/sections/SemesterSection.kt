package hexlay.ums.fragments.sections

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import hexlay.ums.R
import hexlay.ums.helpers.canBeInt
import hexlay.ums.models.subject.Subject
import hexlay.ums.models.subject.SubjectDetail
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters
import kotlinx.android.synthetic.main.layout_subject_detail_item.view.*


class SemesterSection(data: List<Subject>, private val sectionName: String) : Section(
    SectionParameters.builder()
        .itemResourceId(R.layout.subject_view_extend)
        .headerResourceId(R.layout.section_header)
        .build()
) {

    private val subjects = mutableListOf<Subject>()

    init {
        subjects.addAll(data)
    }

    override fun getContentItemsTotal(): Int = subjects.size

    override fun getItemViewHolder(view: View): RecyclerView.ViewHolder {
        return SemesterViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as SemesterViewHolder
        val subject = subjects[position]
        itemHolder.subjectName.text = subject.name
        itemHolder.subjectScore.text = if (subject.score > 0) {
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
        itemHolder.subjectCredit.text = "კრედიტი: ${subject.credit}"
        itemHolder.subjectTeacher.text = subject.lecturer
        when(subject.semesterState) {
            "passed" -> itemHolder.subjectHolder.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.subject_passed)
            "current" -> itemHolder.subjectHolder.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.subject_not_passed)
            else -> itemHolder.subjectHolder.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.subject_failed)
        }
        if (itemHolder.subjectTeacher.text.isEmpty()) {
            itemHolder.subjectTeacher.isGone = true
        }
        if (itemHolder.subjectCredit.text.isEmpty()) {
            itemHolder.subjectCredit.isGone = true
        }
        itemHolder.subjectHolder.setOnClickListener {
            val subjectDetails = subject.details
            if (subjectDetails != null && subjectDetails.isNotEmpty()) {
                val dialog = MaterialDialog(holder.itemView.context, BottomSheet()).customView(R.layout.layout_subject_detail)
                val dialogView = dialog.getCustomView()
                val subjectDetailHolder = dialogView.findViewById<LinearLayout>(R.id.subject_detail_holder)
                for (detail in subjectDetails) {
                    subjectDetailHolder.addView(generateTextView(holder.itemView.context, detail))
                }
                dialog.show {
                    title(text = subject.name)
                }
            }
        }
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

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val itemHolder = holder as HeaderViewHolder
        itemHolder.sectionName.text = sectionName
    }

}