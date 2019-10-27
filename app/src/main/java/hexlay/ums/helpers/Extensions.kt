package hexlay.ums.helpers

import android.text.Html
import android.text.Spanned
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
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
    return if (AppHelper.isNougat) {
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