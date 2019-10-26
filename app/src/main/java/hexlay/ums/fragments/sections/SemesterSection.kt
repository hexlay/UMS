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
        itemHolder.subjectName.text = subject.subjectName
        itemHolder.subjectScore.text = if (subject.subjectScore > 0) {
            if (subject.subjectScore.canBeInt())
                subject.subjectScore.toInt().toString()
            else
                subject.subjectScore.toString()
        } else if(subject.subjectFullScore > 0) {
            if (subject.subjectFullScore.canBeInt())
                "0 (${subject.subjectFullScore.toInt()})"
            else
                "0 (${subject.subjectFullScore})"
        } else {
            "0"
        }
        itemHolder.subjectCredit.text = "კრედიტი: ${subject.subjectCredit}"
        itemHolder.subjectTeacher.text = subject.subjectLecturer
        when(subject.subjectSemesterState) {
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
            val subjectDetails = subject.subjectDetails
            if (subjectDetails != null && subjectDetails.isNotEmpty()) {
                val dialog = MaterialDialog(holder.itemView.context, BottomSheet()).customView(R.layout.layout_subject_detail)
                val dialogView = dialog.getCustomView()
                val subjectDetailHolder = dialogView.findViewById<LinearLayout>(R.id.subject_detail_holder)
                for (detail in subjectDetails) {
                    subjectDetailHolder.addView(generateTextView(holder.itemView.context, detail))
                }
                dialog.show {
                    title(text = subject.subjectName)
                }
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    @SuppressLint("SetTextI18n")
    private fun generateTextView(context: Context, detail: SubjectDetail): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_subject_detail_item, null)
        view.subject_detail_name.text = detail.detailName
        view.subject_detail_name.isSelected = true
        if (detail.detailGrade != null) {
            val gradeValue = if (detail.detailType == "group")
                if (detail.detailGrade.gradeMax.canBeInt())
                    detail.detailGrade.gradeMax.toInt()
                else
                    detail.detailGrade.gradeMax
            else
                if (detail.detailGrade.gradeValue.canBeInt())
                    detail.detailGrade.gradeValue.toInt()
                else
                    detail.detailGrade.gradeValue
            view.subject_detail_result.text = "$gradeValue (${detail.detailMaxScore.toInt()}-დან)"
        } else {
            view.subject_detail_result.text = "_ (${detail.detailMaxScore.toInt()}-დან)"
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