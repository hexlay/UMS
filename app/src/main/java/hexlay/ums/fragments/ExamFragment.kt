package hexlay.ums.fragments

import android.annotation.SuppressLint
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
import hexlay.ums.adapters.ExamAdapter
import hexlay.ums.helpers.setMargins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_score.*
import java.lang.ref.WeakReference

class ExamFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        semester_header.setMargins(top = reference.get()!!.appHelper.statusBarHeight + reference.get()!!.appHelper.dpOf(10))
        semester_header.text = getString(R.string.current_exams)
        score_list.layoutManager = LinearLayoutManager(context)
        score_list_refresher.setOnRefreshListener {
            initExams()
        }
        initExams()
    }

    @SuppressLint("CheckResult")
    private fun initExams() {
        (reference.get()!!.application as UMS).umsAPI.getStudentExams().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                score_list_loader.isGone = true
                score_list.adapter = ExamAdapter(it)
            } else {
                reference.get()!!.disableExams()
            }
            score_list_refresher.isRefreshing = false
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
    }

}