package hexlay.ums.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.graphics.Color
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hexlay.ums.R
import hexlay.ums.activites.StarterActivity
import hexlay.ums.helpers.toHtml
import hexlay.ums.models.notifications.Notification
import hexlay.ums.services.abs.UmsJobService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import org.jetbrains.anko.intentFor

class NotificationService : UmsJobService() {

    private var lastId = "0"
    override var channelId = "UMS_NOTIFICATIONS"

    @SuppressLint("CheckResult")
    override fun sync() {
        lastId = preferenceHelper.lastNotificationId
        umsAPI.getNotifications(limit = "1", state = "unread").observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.notifications.isNotEmpty()) {
                val notification = it.notifications[0]
                val newId = notification.id
                if (lastId != newId) {
                    showNotification(notification)
                }
                preferenceHelper.lastNotificationId = newId
            }
        }, {
            handleError(it)
        })
    }

    private fun showNotification(notification: Notification) {
        createNotificationChannel("Notifications", "Notifications from administration")
        val notificationId = generateId(notification.id)
        val intent = intentFor<StarterActivity>()
        intent.putExtra("notification", notification)
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

}