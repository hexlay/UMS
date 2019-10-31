package hexlay.ums.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.job.JobParameters
import android.app.job.JobService
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.gson.GsonBuilder
import hexlay.ums.R
import hexlay.ums.activites.StarterActivity
import hexlay.ums.api.ForbiddenException
import hexlay.ums.api.UmsAPI
import hexlay.ums.api.interceptors.AddCookiesInterceptor
import hexlay.ums.api.interceptors.ErrorInterceptor
import hexlay.ums.api.interceptors.ReceivedCookiesInterceptor
import hexlay.ums.helpers.PreferenceHelper
import hexlay.ums.helpers.toHtml
import hexlay.ums.models.notifications.Notification
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import okhttp3.OkHttpClient
import org.jetbrains.anko.intentFor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class NotificationService : JobService() {

    private lateinit var preferenceHelper: PreferenceHelper
    private lateinit var umsAPI: UmsAPI

    private var lastId = "0"
    private var newId = "0"
    private val channelId = "UMS"

    override fun onCreate() {
        super.onCreate()
        val client = OkHttpClient.Builder()
            .addInterceptor(ErrorInterceptor(applicationContext))
            .addInterceptor(AddCookiesInterceptor(applicationContext))
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
            .build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(UmsAPI.BASE_URL)
            .build()
        preferenceHelper = PreferenceHelper(applicationContext)
        umsAPI = retrofit.create(UmsAPI::class.java)
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        sync()
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    @SuppressLint("CheckResult")
    private fun sync() {
        lastId = preferenceHelper.lastNotificationId
        umsAPI.getNotifications(limit = "1", state = "unread").observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.notifications.isNotEmpty()) {
                val notification = it.notifications[0]
                newId = notification.id
                if (lastId != newId) {
                    createNotificationChannel()
                    showNotification(notification)
                }
                preferenceHelper.lastNotificationId = newId
            }
        }, {
            handleError(it)
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, "Administration", importance)
            channel.description = "Notifications sent by SANGU administration"
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun handleError(error: Throwable) {
        when (error) {
            is ForbiddenException -> {
                preferenceHelper.clearForLogout()
            }
        }
    }

    private fun showNotification(notification: Notification) {
        val notificationId = generateId(notification.id)
        val intent = intentFor<StarterActivity>()
        val pendingIntent = PendingIntent.getActivity(applicationContext, notificationId, intent, 0)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(notification.data.title)
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setLights(Color.BLUE, 400, 300)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.data.text.toHtml()))
        NotificationManagerCompat.from(applicationContext).notify(notificationId, notificationBuilder.build())
    }

    private fun generateId(id: String): Int {
        val regex = Regex("(\\d+)")
        val numbers = regex.findAll(id)
        return numbers.map { it.value }.joinToString(separator = "").take(4).toInt()
    }

}