package hexlay.ums.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.CalendarMonth
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.MonthHeaderFooterBinder
import hexlay.ums.R
import hexlay.ums.UMS
import hexlay.ums.activites.MainActivity
import hexlay.ums.adapters.CalendarSubjectAdapter
import hexlay.ums.helpers.setMargins
import hexlay.ums.helpers.setTextColorRes
import hexlay.ums.models.session.Session
import hexlay.ums.views.DayViewContainer
import hexlay.ums.views.MonthViewContainer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter

class CalendarFragment : Fragment() {

    private var reference: MainActivity? = null
    private var calendarSubjectAdapter: CalendarSubjectAdapter? = null
    private var savedSessions: List<Session>? = null
    private var disposable: CompositeDisposable? = null

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        disposable = CompositeDisposable()
        reference = activity as MainActivity
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        calendarSubjectAdapter = CalendarSubjectAdapter()
        val topMargin = reference?.appHelper?.statusBarHeight!! + reference?.appHelper?.dpOf(5)!!
        calendar_view.setMargins(top = topMargin)
        calendar_refresher.setOnRefreshListener {
            initSessions()
        }
        initSessions()
    }

    private fun setupRecyclerView() {
        subject_view.layoutManager = LinearLayoutManager(context)
        subject_view.adapter = calendarSubjectAdapter
    }

    private fun setupCalendar() {
        val currentMonth = YearMonth.now()
        val firstMonth = currentMonth.minusMonths(10)
        val lastMonth = currentMonth.plusMonths(10)
        calendar_view.setup(firstMonth, lastMonth, DayOfWeek.MONDAY)
        calendar_view.scrollToMonth(currentMonth)
        selectDate(today)
    }

    private fun setContainers() {
        calendar_view.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view, this@CalendarFragment)
            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.dayText.text = day.date.dayOfMonth.toString()

                if (day.owner == DayOwner.THIS_MONTH) {
                    container.dayText.isVisible = true
                    when (day.date) {
                        today -> {
                            container.dayText.setTextColorRes(android.R.color.white)
                            container.dayText.setBackgroundResource(R.drawable.today)
                            container.dotView.isVisible = false
                        }
                        selectedDate -> {
                            container.dayText.setBackgroundResource(R.drawable.selected)
                            container.dotView.isVisible = false
                        }
                        else -> {
                            val currentSubjects = savedSessions?.filter { it.dayOfWeek == day.date.dayOfWeek.value }?.toList()
                            currentSubjects?.let {
                                container.dayText.background = null
                                container.dotView.isVisible = it.isNotEmpty()
                            }
                        }
                    }
                } else {
                    container.dayText.isVisible = false
                    container.dotView.isVisible = false
                }
            }
        }
        calendar_view.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)
            override fun bind(container: MonthViewContainer, month: CalendarMonth) {}
        }
    }

    fun selectDate(date: LocalDate) {
        if (selectedDate != date) {
            val oldDate = selectedDate
            selectedDate = date
            oldDate?.let { calendar_view.notifyDateChanged(it) }
            calendar_view.notifyDateChanged(date)
            selected_date.text = selectionFormatter.format(date)
            val currentSubjects = savedSessions?.filter { it.dayOfWeek == date.dayOfWeek.value }?.toList()
            currentSubjects?.let {
                calendarSubjectAdapter?.changeSubjects(it)
                if (it.isEmpty()) {
                    calendar_dnd.isGone = false
                    subject_view.isGone = true
                } else {
                    calendar_dnd.isGone = true
                    subject_view.isGone = false
                }
            }
        }
    }

    private fun initSessions() {
        val method = (reference?.application as UMS).umsAPI.getStudentSessions().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                savedSessions = it
                setContainers()
                setupCalendar()
                setupRecyclerView()
            }
            calendar_refresher.isRefreshing = false
        }, {
            (reference?.application as UMS).handleError(it)
        })
        disposable?.add(method)
    }

    override fun onDestroyView() {
        disposable?.dispose()
        super.onDestroyView()
    }

}