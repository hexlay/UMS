package hexlay.ums.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
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
import hexlay.ums.helpers.PreferenceHelper
import hexlay.ums.helpers.setMargins
import hexlay.ums.helpers.setUrl
import hexlay.ums.models.Profile
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_change_profile.*
import java.lang.ref.WeakReference

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
        all_semester_subjects.setPadding(0, 0, 0, reference.get()!!.appHelper.actionBarSize)
        initSubjects()
        initProfile()
    }

    @SuppressLint("CheckResult")
    private fun initSubjects() {
        (reference.get()!!.application as UMS).umsAPI.getTotalStudentSubjects().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe {
            if (it.isNotEmpty()) {
                val map = it.sortedWith(compareByDescending { it.subjectSemester }).groupBy { it.subjectSemester }
                for ((key, value) in map) {
                    val semesterName = if (key > 0) "სემესტრი $key" else "აღიარებული საგნები"
                    semesterAdapter.addSection(SemesterSection(value, semesterName))
                }
                all_semester_subjects.adapter = semesterAdapter
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initProfile() {
        val topMargin = reference.get()!!.appHelper.statusBarHeight + reference.get()!!.appHelper.dpOf(10)
        profile_card.setMargins(reference.get()!!.appHelper.dpOf(5), topMargin, reference.get()!!.appHelper.dpOf(5), 0)
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
            (reference.get()!!.application as UMS).umsAPI.logout().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe {
                if (profile != null && profile.exists()) {
                    profile.delete()
                }
                PreferenceHelper(context!!).clear()
                reference.get()!!.exitMainActivity()
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
                    if (oldPassword.text!!.isEmpty() or newPassword.text!!.isEmpty()) {
                        current_password_input.error = resources.getString(R.string.auth_empty)
                        new_password_input.error = resources.getString(R.string.auth_empty)
                    } else {
                        //TODO: Api Change Password
                        dialog.dismiss()
                    }
                }
            }
        }
    }

}