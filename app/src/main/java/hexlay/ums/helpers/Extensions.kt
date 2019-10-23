package hexlay.ums.helpers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import hexlay.ums.R
import org.threeten.bp.DayOfWeek
import org.threeten.bp.temporal.WeekFields
import java.security.MessageDigest
import java.util.*

fun View.setMargins(left: Int? = null, top: Int? = null, right: Int? = null, bottom: Int? = null) {
    val params = layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
        left ?: params.leftMargin,
        top ?: params.topMargin,
        right ?: params.rightMargin,
        bottom ?: params.rightMargin
    )
    layoutParams = params
}

fun String.md5(): String {
    val digested = MessageDigest.getInstance("MD5").digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}

fun ImageView.setUrl(url: String) {
    Glide.with(this.context)
        .load(url)
        .into(this)
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInVisible() {
    visibility = View.INVISIBLE
}

fun View.setSize(width: Int? = null, height: Int? = null) {
    layoutParams.width = width ?: layoutParams.width
    layoutParams.height = height ?: layoutParams.height
}

internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

fun daysOfWeekFromLocale(): Array<DayOfWeek> {
    val firstDayOfWeek = WeekFields.of(Locale.getDefault()).firstDayOfWeek
    var daysOfWeek = DayOfWeek.values()
    if (firstDayOfWeek != DayOfWeek.MONDAY) {
        val rhs = daysOfWeek.sliceArray(firstDayOfWeek.ordinal..daysOfWeek.indices.last)
        val lhs = daysOfWeek.sliceArray(0 until firstDayOfWeek.ordinal)
        daysOfWeek = rhs + lhs
    }
    return daysOfWeek
}