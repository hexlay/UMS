package hexlay.ums.fragments.sections

import android.view.View
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import hexlay.ums.R
import hexlay.ums.models.subject.Subject
import io.github.luizgrp.sectionedrecyclerviewadapter.Section
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters


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

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val itemHolder = holder as SemesterViewHolder
        itemHolder.subjectName.text = subjects[position].subjectName
        itemHolder.subjectScore.text = subjects[position].subjectScore.toString()
        itemHolder.subjectCredit.text = "საგნის კრედიტი: ${subjects[position].subjectCredit}"
        itemHolder.subjectTeacher.text = subjects[position].subjectLecturer
        if (itemHolder.subjectTeacher.text.isEmpty()) {
            itemHolder.subjectTeacher.isGone = true
        }
        if (itemHolder.subjectCredit.text.isEmpty()) {
            itemHolder.subjectCredit.isGone = true
        }
    }

    override fun getHeaderViewHolder(view: View): RecyclerView.ViewHolder {
        return HeaderViewHolder(view)
    }

    override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder?) {
        val itemHolder = holder as HeaderViewHolder
        itemHolder.sectionName.text = sectionName
    }

}