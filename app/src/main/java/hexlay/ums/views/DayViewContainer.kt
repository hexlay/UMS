package hexlay.ums.views

import android.view.View
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.ViewContainer
import hexlay.ums.fragments.CalendarFragment
import kotlinx.android.synthetic.main.calendar_day_layout.view.*

class DayViewContainer(view: View, calendarFragment: CalendarFragment) : ViewContainer(view) {
    lateinit var day: CalendarDay
    val dayText = view.day_text!!
    val dotView = view.day_dot!!

    init {
        view.setOnClickListener {
            if (day.owner == DayOwner.THIS_MONTH) {
                calendarFragment.selectDate(day.date)
            }
        }
    }
}