package hexlay.ums.helpers

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import androidx.core.content.ContextCompat
import hexlay.ums.R
import java.lang.ref.WeakReference

class AppHelper(activity: Activity) {

    private val reference: WeakReference<Activity> = WeakReference(activity)

    val statusBarHeight: Int
        get() {
            val resourceId = reference.get()!!.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) reference.get()!!.resources.getDimensionPixelSize(resourceId) else 0
        }

    val actionBarSize: Int
        get() {
            val styledAttributes = reference.get()!!.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val dimension = styledAttributes.getDimension(0, 0F).toInt()
            styledAttributes.recycle()
            return dimension
        }


    fun dpOf(value: Int): Int {
        val scale = reference.get()!!.resources.displayMetrics.density
        return (value * scale + 0.5f).toInt()
    }

    fun makeFullscreen() {
        val decorView = reference.get()!!.window.decorView
        var flags = decorView.systemUiVisibility
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        flags = flags or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        decorView.systemUiVisibility = flags
        paintNavigationBar()
    }

    private fun paintNavigationBar() {
        if (isOreo) {
            val decorView = reference.get()!!.window.decorView
            var flags = decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            decorView.systemUiVisibility = flags
            reference.get()!!.window.navigationBarColor = ContextCompat.getColor(reference.get()!!, R.color.background)
        } else {
            reference.get()!!.window.navigationBarColor = Color.GRAY
        }
    }

    companion object {

        val isPie: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        val isOreo: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

        val isNougat: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    }

}