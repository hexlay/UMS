package hexlay.ums.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.SubjectAdapter
import hexlay.ums.helpers.setMargins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_score.*
import java.lang.ref.WeakReference

class ScoreFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        semester_header.setMargins(top = reference.get()!!.appHelper.statusBarHeight + reference.get()!!.appHelper.dpOf(10))
        score_list.setPadding(0, 0, 0, reference.get()!!.appHelper.actionBarSize)
        score_list.layoutManager = LinearLayoutManager(context)
        initSubjects()
    }

    @SuppressLint("CheckResult")
    private fun initSubjects() {
        (reference.get()!!.application as UMS).umsAPI.getCurrentStudentSubjects().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe {
            if (it.isNotEmpty()) {
                score_list.adapter = SubjectAdapter(it)
            }
        }
    }

}