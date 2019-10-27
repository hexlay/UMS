package hexlay.ums.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.dbflow5.query.result
import com.dbflow5.query.select
import com.dbflow5.structure.delete
import com.dbflow5.structure.exists
import com.google.android.material.textfield.TextInputEditText
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.activites.MainActivity
import hexlay.ums.api.UmsAPI
import hexlay.ums.fragments.sections.SemesterSection
import hexlay.ums.helpers.*
import hexlay.ums.models.Profile
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_change_profile.*
import kotlinx.android.synthetic.main.layout_profile_totals.view.*
import org.jetbrains.anko.support.v4.toast
import java.lang.ref.WeakReference

@SuppressLint("CheckResult")
class ProfileFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>
    private lateinit var semesterAdapter: SectionedRecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        semesterAdapter = SectionedRecyclerViewAdapter()
        all_semester_subjects.layoutManager = LinearLayoutManager(context)
        initSubjects()
        initProfile()
        initTotals()
    }

    private fun initSubjects() {
        (reference.get()!!.application as UMS).umsAPI.getTotalStudentSubjects().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                all_semester_subjects_loader.isGone = true
                val creditsSum = it.filter { sItSem -> sItSem.subjectSemesterState == "passed" }.sumBy { sItCred -> sItCred.subjectCredit }
                total_progress_holder.addView(generateTotalsView(creditsSum.toDouble(), 240))
                val subjectMap = it.sortedWith(compareByDescending { sIt -> sIt.subjectSemester }).groupBy { gIt -> gIt.subjectSemester }
                for ((key, value) in subjectMap) {
                    val semesterName = if (key > 0) "სემესტრი $key" else "აღიარებული საგნები"
                    semesterAdapter.addSection(SemesterSection(value, semesterName))
                }
                all_semester_subjects.adapter = semesterAdapter
                initPreviousCourseSubjects()
            }
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
    }

    private fun initPreviousCourseSubjects() {
        val profile = (select from Profile::class).result
        if (profile?.id != null) {
            (reference.get()!!.application as UMS).umsAPI.getTotalStudentSubjectsPrevijous(profile.id!!).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
                if (it.isNotEmpty()) {
                    val subjectMap = it.sortedWith(compareByDescending { sIt -> sIt.subjectSemester }).groupBy { gIt -> gIt.subjectSemester }
                    for ((key, value) in subjectMap) {
                        val semesterName = if (key > 0) "სემესტრი $key (მობილობამდე)" else "აღიარებული საგნები (მობილობამდე)"
                        semesterAdapter.addSection(SemesterSection(value, semesterName))
                    }
                    semesterAdapter.notifyDataSetChanged()
                }
            }, {
                (reference.get()!!.application as UMS).handleError(it)
            })
        }
    }

    @SuppressLint("CheckResult", "SetTextI18n")
    private fun initProfile() {
        val profile = (select from Profile::class).result
        if (profile != null) {
            profile_name.text = "${profile.firstName} ${profile.lastName}"
            profile_status.text = profile.email
            profile_image.setUrl(UmsAPI.BASE_URL + profile.photoUrl!!)
            if (profile.gender == "male") {
                profile_image.shadowColor = Color.RED
                profile_image.borderColor = Color.RED
            } else {
                profile_image.shadowColor = Color.MAGENTA
                profile_image.borderColor = Color.MAGENTA
            }
        }
        logout.setOnClickListener {
            MaterialDialog(context!!).show {
                message(R.string.profile_logout_prompt)
                positiveButton(R.string.yes) {
                    (reference.get()!!.application as UMS).umsAPI.logout().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
                        if (profile != null && profile.exists()) {
                            profile.delete()
                        }
                        PreferenceHelper(context).clear()
                        reference.get()!!.exitMainActivity()
                    }, {
                        (reference.get()!!.application as UMS).handleError(it)
                    })
                }
                negativeButton(R.string.no) {}
            }
        }
        edit_profile.setOnClickListener {
            val dialog = MaterialDialog(context!!).customView(R.layout.layout_change_profile)
            val dialogView = dialog.getCustomView()
            val oldPassword = dialogView.findViewById<TextInputEditText>(R.id.old_password)
            val newPassword = dialogView.findViewById<TextInputEditText>(R.id.new_password)
            dialog.show {
                title(R.string.profile_edit)
                noAutoDismiss()
                positiveButton(R.string.profile_change_submit) {
                    val oldPasswordText = oldPassword.text.toString()
                    val newPasswordText = newPassword.text.toString()
                    when {
                        oldPasswordText.isEmpty() or newPasswordText.isEmpty() -> {
                            current_password_input.error = resources.getString(R.string.auth_empty)
                            new_password_input.error = resources.getString(R.string.auth_empty)
                        }
                        oldPasswordText.md5() != reference.get()!!.preferenceHelper.passwordHash -> current_password_input.error = resources.getString(R.string.profile_change_password_error)
                        else -> {
                            (reference.get()!!.application as UMS).umsAPI.passwordChange(newPasswordText).observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
                                reference.get()!!.preferenceHelper.passwordHash = newPasswordText.md5()
                                toast(R.string.profile_change_success)
                                dialog.dismiss()
                            }, {
                                (reference.get()!!.application as UMS).handleError(it)
                            })
                        }
                    }
                }
            }
        }
        dark_mode.setOnClickListener {
            val darkModeItems = mutableListOf("Bring the light", "Fall in the darkness", "Follow system")
            if (!AppHelper.isPie) {
                darkModeItems.removeAt(2)
            }
            MaterialDialog(context!!).show {
                title(R.string.profile_change_theme_title)
                listItemsSingleChoice(items = darkModeItems, initialSelection = reference.get()!!.preferenceHelper.darkMode) { _, index, _ ->
                    reference.get()!!.preferenceHelper.darkMode = index
                    reference.get()!!.initAppTheme()
                }
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun initTotals() {
        (reference.get()!!.application as UMS).umsAPI.getStudentTotals().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it != null) {
                total_progress_holder.addView(generateTotalsView(it.currentAverage, 100))
                total_progress_holder.addView(generateTotalsView(it.currentGpa, 4))
            }
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
    }

    private fun generateTotalsView(data: Double, max: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_profile_totals, null)
        view.total_progress.max = max
        view.total_progress.progress = data.toInt()
        view.total_progress_text.text = if (data.canBeInt()) data.toInt().toString() else data.toString()
        return view
    }

}