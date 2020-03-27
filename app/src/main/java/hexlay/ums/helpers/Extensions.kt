package hexlay.ums.helpers

import android.app.Activity
import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import hexlay.ums.services.events.SubscriptionError
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.internal.schedulers.IoScheduler
import org.greenrobot.eventbus.EventBus
import java.security.MessageDigest

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

fun String.toHtml(): Spanned {
    return if (Constants.isNougat) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}

fun String.md5(): String {
    val digested = MessageDigest.getInstance("MD5").digest(toByteArray())
    return digested.joinToString("") {
        String.format("%02x", it)
    }
}

fun Double.canBeInt(): Boolean = this - toInt() == 0.0

fun ImageView.setUrl(url: String) = Glide.with(context).load(url).into(this)

fun View.setSize(width: Int? = null, height: Int? = null) {
    layoutParams.width = width ?: layoutParams.width
    layoutParams.height = height ?: layoutParams.height
}

fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(ContextCompat.getColor(context, color))

fun Activity.dpOf(value: Int): Int {
    val scale = resources.displayMetrics.density
    return (value * scale + 0.5f).toInt()
}

fun Activity.dpOf(value: Float): Float {
    val scale = resources.displayMetrics.density
    return (value * scale + 0.5f)
}

fun Activity.getStatusBarHeight(): Int {
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return if (resourceId > 0) resources.getDimensionPixelSize(resourceId) else 0
}

fun Activity.getActionBarSize(): Int {
    val styledAttributes = theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
    val dimension = styledAttributes.getDimension(0, 0F).toInt()
    styledAttributes.recycle()
    return dimension
}

fun Fragment.getStatusBarHeight(): Int = requireActivity().getStatusBarHeight()

fun Fragment.getActionBarSize(): Int = requireActivity().getActionBarSize()

fun Fragment.dpOf(value: Int): Int = requireActivity().dpOf(value)

fun <T> Observable<T>.observe(onResult: (T) -> Unit): Disposable {
    return observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
        onResult(it)
    }, {
        EventBus.getDefault().post(SubscriptionError(it))
    })
}