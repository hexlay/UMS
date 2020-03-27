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
import hexlay.ums.adapters.ExamAdapter
import hexlay.ums.api.Api
import hexlay.ums.helpers.dpOf
import hexlay.ums.helpers.getStatusBarHeight
import hexlay.ums.helpers.observe
import hexlay.ums.helpers.setMargins
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_exams.*

class ExamFragment : Fragment() {

    private var disposable: CompositeDisposable? = null
    private lateinit var activity: MainActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        activity = requireActivity() as MainActivity
        return inflater.inflate(R.layout.fragment_exams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exam_header.setMargins(top = getStatusBarHeight() + dpOf(10))
        exam_list.layoutManager = LinearLayoutManager(context)
        initExams()
    }

    private fun initExams() {
        disposable?.add(Api.make(requireContext()).getStudentExams().observe {
            exam_list_loader.isGone = true
            exam_list.adapter = ExamAdapter(it)
        })
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

}