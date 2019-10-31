package hexlay.ums.fragments

import android.annotation.SuppressLint
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
import io.reactivex.internal.schedulers.IoScheduler
import kotlinx.android.synthetic.main.fragment_calendar.*
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import java.lang.ref.WeakReference

class CalendarFragment : Fragment() {

    private lateinit var reference: WeakReference<MainActivity>
    private lateinit var calendarSubjectAdapter: CalendarSubjectAdapter
    private lateinit var savedSessions: List<Session>

    private var selectedDate: LocalDate? = null
    private val today = LocalDate.now()
    private val selectionFormatter = DateTimeFormatter.ofPattern("d MMM yyyy")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reference = WeakReference(activity as MainActivity)
        calendarSubjectAdapter = CalendarSubjectAdapter()
        val topMargin = reference.get()!!.appHelper.statusBarHeight + reference.get()!!.appHelper.dpOf(5)
        calendar_view.setMargins(top = topMargin)
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
                            val currentSubjects = savedSessions.filter { it.dayOfWeek == day.date.dayOfWeek.value }.toList()
                            container.dayText.background = null
                            container.dotView.isVisible = currentSubjects.isNotEmpty()
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
            val currentSubjects = savedSessions.filter { it.dayOfWeek == date.dayOfWeek.value }.toList()
            calendarSubjectAdapter.changeSubjects(currentSubjects)
            if (currentSubjects.isEmpty()) {
                calendar_dnd.isGone = false
                subject_view.isGone = true
            } else {
                calendar_dnd.isGone = true
                subject_view.isGone = false
            }
        }
    }

    @SuppressLint("CheckResult")
    private fun initSessions() {
        (reference.get()!!.application as UMS).umsAPI.getStudentSessions().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            savedSessions = it
            setContainers()
            setupCalendar()
            setupRecyclerView()
        }, {
            (reference.get()!!.application as UMS).handleError(it)
        })
    }

}