package hexlay.ums.helpers

import android.annotation.SuppressLint
import android.app.job.JobScheduler
import android.content.Context
import android.content.Context.JOB_SCHEDULER_SERVICE
import android.net.NetworkCapabilities
import android.os.Build
import org.jetbrains.anko.connectivityManager

class AppHelper(val context: Context) {

    val statusBarHeight: Int
        get() {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            return if (resourceId > 0) context.resources.getDimensionPixelSize(resourceId) else 0
        }

    val actionBarSize: Int
        get() {
            val styledAttributes = context.theme.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            val dimension = styledAttributes.getDimension(0, 0F).toInt()
            styledAttributes.recycle()
            return dimension
        }

    val isSyncing: Boolean
        @SuppressLint("NewApi")
        get() {
            val scheduler = context.getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
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
        val scale = context.resources.displayMetrics.density
        return (value * scale + 0.5f).toInt()
    }

    fun isNetworkAvailable(): Boolean {
        val network = context.connectivityManager.activeNetwork
        val capabilities = context.connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

    companion object {

        val isPie: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

        val isNougat: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    }

}