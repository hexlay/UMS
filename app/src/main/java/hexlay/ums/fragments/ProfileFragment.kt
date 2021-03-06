package hexlay.ums.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.dbflow5.query.result
import com.dbflow5.query.select
import com.dbflow5.structure.delete
import com.dbflow5.structure.exists
import hexlay.ums.R
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.sections.SemesterSection
import hexlay.ums.api.Api
import hexlay.ums.api.UmsAPI
import hexlay.ums.helpers.*
import hexlay.ums.models.Profile
import hexlay.ums.services.events.ConnectedSuccessEvent
import hexlay.ums.services.events.ConnectedUnSuccessEvent
import hexlay.ums.services.events.Event
import hexlay.ums.services.events.LogoutEvent
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.layout_change_profile.*
import kotlinx.android.synthetic.main.layout_profile_totals.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.connectivityManager
import org.jetbrains.anko.support.v4.toast

class ProfileFragment : Fragment() {

    private lateinit var activity: MainActivity
    private var semesterAdapter: SectionedRecyclerViewAdapter? = null
    private var disposable: CompositeDisposable? = null
    private lateinit var api: UmsAPI

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        activity = requireActivity() as MainActivity
        api = Api.make(requireContext())
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        semesterAdapter = SectionedRecyclerViewAdapter()
        all_semester_subjects.layoutManager = LinearLayoutManager(context)
        initEvents()
        initSubjects()
        initProfile()
        initTotals()
    }

    private fun initEvents() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this)
        }
    }

    private fun initSubjects() {
        disposable?.add(api.getTotalStudentSubjects().observe {
            if (it.isNotEmpty()) {
                all_semester_subjects_loader.isGone = true
                val creditsSum = it.filter { sItSem -> sItSem.semesterState == "passed" }.sumBy { sItCred -> sItCred.credit }
                total_progress_holder.addView(generateTotalsView(creditsSum.toDouble(), 240))
                val subjectMap = it.sortedWith(compareByDescending { sIt -> sIt.semester }).groupBy { gIt -> gIt.semester }
                for ((key, value) in subjectMap) {
                    val semesterName = if (key > 0) "სემესტრი $key" else "აღიარებული საგნები"
                    semesterAdapter?.addSection(SemesterSection(value, semesterName))
                }
                all_semester_subjects.adapter = semesterAdapter
                initPreviousCourseSubjects()
            }
        })
    }

    private fun initPreviousCourseSubjects() {
        val profile = (select from Profile::class).result
        if (profile?.id != null) {
            disposable?.add(api.getTotalStudentSubjectsPrevijous(profile.id!!).observe {
                if (it.isNotEmpty()) {
                    val subjectMap = it.sortedWith(compareByDescending { sIt -> sIt.semester }).groupBy { gIt -> gIt.semester }
                    for ((key, value) in subjectMap) {
                        val semesterName = if (key > 0) "სემესტრი $key (მობილობამდე)" else "აღიარებული საგნები (მობილობამდე)"
                        semesterAdapter?.addSection(SemesterSection(value, semesterName))
                    }
                    semesterAdapter?.notifyDataSetChanged()
                }
            })
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initProfile() {
        val profile = (select from Profile::class).result
        profile?.let { profileResult ->
            profile_name.text = "${profileResult.firstName} ${profileResult.lastName}"
            profile_status.text = profileResult.email
            profileResult.photoUrl?.let {
                profile_image.setUrl(UmsAPI.BASE_URL + it)
            }
            if (profileResult.gender == "male") {
                profile_image.borderColor = Color.RED
            } else {
                profile_image.borderColor = Color.MAGENTA
            }
        }
        logout.setOnClickListener {
            MaterialDialog(context!!).show {
                message(R.string.profile_logout_prompt)
                positiveButton(R.string.yes) {
                    disposable?.add(api.logout().observe {
                        if (profile != null && profile.exists()) {
                            profile.delete()
                        }
                        PreferenceHelper(context).clearForLogout()
                        EventBus.getDefault().post(LogoutEvent())
                    })
                }
                negativeButton(R.string.no) {}
            }
        }
        edit_profile.setOnClickListener {
            val dialog = MaterialDialog(context!!).customView(R.layout.layout_change_profile)
            dialog.show {
                title(R.string.profile_edit)
                noAutoDismiss()
                positiveButton(R.string.profile_change_submit) {
                    val oldPasswordText = old_password.text.toString()
                    val newPasswordText = new_password.text.toString()
                    when {
                        oldPasswordText.isEmpty() or newPasswordText.isEmpty() -> {
                            current_password_input.error = resources.getString(R.string.auth_empty)
                            new_password_input.error = resources.getString(R.string.auth_empty)
                        }
                        oldPasswordText.md5() != activity.preferenceHelper.passwordHash -> current_password_input.error = resources.getString(R.string.profile_change_password_error)
                        else -> {
                            profile_change_loading.isVisible = true
                            disposable?.add(api.passwordChange(newPasswordText).observe {
                                activity.preferenceHelper.passwordHash = newPasswordText.md5()
                                toast(R.string.profile_change_success)
                                profile_change_loading.isVisible = false
                                dialog.dismiss()
                            })
                        }
                    }
                }
            }
        }
        dark_mode.setOnClickListener {
            val darkModeItems = mutableListOf("Just white", "Blackish", "Follow system")
            if (!Constants.isPie) {
                darkModeItems.removeAt(2)
            }
            MaterialDialog(context!!).show {
                title(R.string.profile_change_theme_title)
                listItemsSingleChoice(items = darkModeItems, initialSelection = activity.preferenceHelper.darkMode) { _, index, _ ->
                    activity.preferenceHelper.darkMode = index
                    activity.applyDayNight()
                }
            }
        }
        if (!isNetworkAvailable()) {
            edit_profile.hide()
            logout.hide()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val network = requireContext().connectivityManager.activeNetwork
        val capabilities = requireContext().connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    private fun initTotals() {
        disposable?.add(api.getStudentTotals().observe {
            total_progress_holder.addView(generateTotalsView(it.currentAverage, 100))
            total_progress_holder.addView(generateTotalsView(it.currentGpa, 4))
        })
    }

    @SuppressLint("InflateParams")
    private fun generateTotalsView(data: Double, max: Int): View {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_profile_totals, null)
        view.total_progress.max = max
        view.total_progress.progress = data.toInt()
        view.total_progress_text.text = if (data.canBeInt()) data.toInt().toString() else data.toString()
        return view
    }

    @Subscribe
    fun onEvent(event: Event) {
        when (event) {
            is ConnectedSuccessEvent -> {
                edit_profile.show()
                logout.show()
            }
            is ConnectedUnSuccessEvent -> {
                edit_profile.hide()
                logout.hide()
            }
        }
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        disposable?.dispose()
        super.onDestroyView()
    }

}