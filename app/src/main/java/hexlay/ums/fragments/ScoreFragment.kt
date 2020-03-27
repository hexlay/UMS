package hexlay.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hexlay.ums.R
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.SubjectAdapter
import hexlay.ums.api.Api
import hexlay.ums.helpers.*
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_score.*

class ScoreFragment : Fragment() {

    private lateinit var activity: MainActivity
    private var disposable: CompositeDisposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        activity = requireActivity() as MainActivity
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        semester_header.setMargins(top = getStatusBarHeight() + dpOf(10))
        score_list.layoutManager = LinearLayoutManager(context)
        score_list_refresher.setProgressViewOffset(false, 0, getActionBarSize())
        score_list_refresher.setOnRefreshListener {
            initSubjects()
        }
        initSubjects()
        initNotificationCheck()
    }

    private fun initSubjects() {
        disposable?.add(Api.make(requireContext()).getCurrentStudentSubjects().observe {
            if (it.isNotEmpty()) {
                score_list_loader.isGone = true
                score_list.adapter = SubjectAdapter(it)
            }
            score_list_refresher.isRefreshing = false
        })
    }

    private fun initNotificationCheck() {
        receive_notification_scores.isChecked = activity.preferenceHelper.getNotificationsScore
        receive_notification_scores.setOnCheckedChangeListener { _, isChecked ->
            activity.preferenceHelper.getNotificationsScore = isChecked
            if (isChecked) {
                activity.startScoreJob()
            } else {
                activity.stopJob(0x2)
            }
        }
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

}