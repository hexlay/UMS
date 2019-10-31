package hexlay.ums.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.app.job.JobScheduler
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.os.Build
import android.view.View
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

    val isSyncing: Boolean
        @SuppressLint("NewApi")
        get() {
            val scheduler = reference.get()!!.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
            if (isNougat) {
                return scheduler.getPendingJob(0x1) != null
            } else {
                for (jobInfo in scheduler.allPendingJobs) {
                    if (jobInfo.id == 0x1) {
                        return true
                    }
                }
                return false
            }
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
    }

    companion object {

        val isPie: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        val isNougat: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    }

}