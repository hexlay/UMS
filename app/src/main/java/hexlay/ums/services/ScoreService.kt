package hexlay.ums.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.graphics.Color
import android.provider.Settings
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import hexlay.ums.R
import hexlay.ums.activites.StarterActivity
import hexlay.ums.helpers.canBeInt
import hexlay.ums.services.abs.UmsJobService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.schedulers.IoScheduler
import org.jetbrains.anko.intentFor

class ScoreService : UmsJobService() {

    override var channelId = "UMS_SCORES"

    @SuppressLint("CheckResult")
    override fun sync() {
        umsAPI.getCurrentStudentSubjects().observeOn(AndroidSchedulers.mainThread()).subscribeOn(IoScheduler()).subscribe({
            if (it.isNotEmpty()) {
                for (subject in it) {
                    val oldValue = preferenceHelper.getCustom(subject.id)
                    val newValue = if (subject.score > 0) {
                        if (subject.score.canBeInt())
                            subject.score.toInt().toString()
                        else
                            subject.score.toString()
                    } else if(subject.fullScore > 0) {
                        if (subject.fullScore.canBeInt())
                            subject.fullScore.toInt().toString()
                        else
                            subject.fullScore.toString()
                    } else {
                        "0"
                    }
                    if (oldValue != newValue) {
                        showNotification(subject.id, subject.name, newValue)
                    }
                    preferenceHelper.putCustom(subject.id, newValue)
                }
            }
        }, {
            handleError(it)
        })
    }

    private fun showNotification(id: String, subjectName: String, subjectScore: String) {
        createNotificationChannel("Scores", "Notifications about score change")
        val notificationId = generateId(id)
        val pendingIntent = PendingIntent.getActivity(applicationContext, notificationId, intentFor<StarterActivity>(), 0)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle("თქვენს ქულებში ცვლილებაა !")
            .setSmallIcon(R.drawable.ic_notification_icon)
            .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            .setLights(Color.RED, 400, 300)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup("GROUP_SCORES")
            .setStyle(NotificationCompat.BigTextStyle().bigText("$subjectName: $subjectScore"))
        NotificationManagerCompat.from(applicationContext).notify(notificationId, notificationBuilder.build())
    }

}