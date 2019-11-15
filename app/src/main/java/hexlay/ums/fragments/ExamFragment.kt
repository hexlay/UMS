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
import hexlay.ums.adapters.ExamAdapter
import hexlay.ums.helpers.setMargins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_exams.*
import java.lang.ref.WeakReference

class ExamFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>
    private lateinit var disposable: CompositeDisposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        return inflater.inflate(R.layout.fragment_exams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        exam_header.setMargins(top = reference.get()!!.appHelper.statusBarHeight + reference.get()!!.appHelper.dpOf(10))
        exam_list.layoutManager = LinearLayoutManager(context)
        initExams()
    }

    private fun initExams() {
        val method = (reference.get()!!.application as UMS).umsAPI.getStudentExams().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                exam_list_loader.isGone = true
                exam_list.adapter = ExamAdapter(it)
            } else {
                reference.get()!!.disableExams()
            }
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
        disposable.add(method)
    }

    override fun onDestroyView() {
        disposable.dispose()
        super.onDestroyView()
    }

}