package hexlay.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.SubjectAdapter
import hexlay.ums.helpers.setMargins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_score.*

class ScoreFragment : Fragment() {

    private var reference: MainActivity? = null
    private var disposable: CompositeDisposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        reference = activity as MainActivity
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        semester_header.setMargins(top = reference?.appHelper?.statusBarHeight!! + reference?.appHelper?.dpOf(10)!!)
        score_list.layoutManager = LinearLayoutManager(context)
        score_list_refresher.setProgressViewOffset(false, 0, reference?.appHelper?.actionBarSize!!)
        score_list_refresher.setOnRefreshListener {
            initSubjects()
        }
        initSubjects()
        initNotificationCheck()
    }

    private fun initSubjects() {
        val method = (reference?.application as UMS).umsAPI.getCurrentStudentSubjects().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                score_list_loader.isGone = true
                score_list.adapter = SubjectAdapter(it)
            }
            score_list_refresher.isRefreshing = false
        }, {
            (reference?.application as UMS).handleError(it)
        })
        disposable?.add(method)
    }

    private fun initNotificationCheck() {
        receive_notification_scores.isChecked = reference?.preferenceHelper?.getNotificationsScore!!
        receive_notification_scores.setOnCheckedChangeListener { _, isChecked ->
            reference?.preferenceHelper?.getNotificationsScore = isChecked
            if (isChecked) {
                reference?.startScoreJob()
            } else {
                reference?.stopJob(0x2)
            }
        }
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

}