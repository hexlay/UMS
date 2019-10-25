package hexlay.ums.helpers.acra

import android.content.Context
import android.os.AsyncTask
import hexlay.ums.R
import org.acra.ReportField
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender
import java.lang.ref.WeakReference
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailSender : ReportSender {

    override fun send(context: Context, errorContent: CrashReportData) {
        SendEmail(context).execute(errorContent)
    }

    companion object {
        class SendEmail(context: Context) : AsyncTask<CrashReportData, Void, Void>() {

            private val contextReference: WeakReference<Context> = WeakReference(context)

            override fun doInBackground(vararg errorContents: CrashReportData): Void? {
                val properties = Properties()
                properties["mail.smtp.host"] = "smtp.gmail.com"
                properties["mail.smtp.socketFactory.port"] = "465"
                properties["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                properties["mail.smtp.layout_auth"] = "true"
                properties["mail.smtp.auth"] = "true"
                properties["mail.smtp.port"] = "465"

                val session = Session.getDefaultInstance(properties, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("dummyforsend@gmail.com", "VeryVeryDummy1")
                    }
                })
                session.debug = true

                val errorContent = errorContents[0]
                var crashContent = "<b>Date:</b> ${errorContent.getString(ReportField.USER_APP_START_DATE)} -> ${errorContent.getString(ReportField.USER_CRASH_DATE)} <br>"
                crashContent += "<b>Phone model:</b> ${errorContent.getString(ReportField.BRAND)} - ${errorContent.getString(ReportField.PHONE_MODEL)} <br>"
                crashContent += "<b>App version:</b> ${errorContent.getString(ReportField.APP_VERSION_NAME)} - ${errorContent.getString(ReportField.APP_VERSION_CODE)} <br>"
                crashContent += "<b>Android version:</b> ${errorContent.getString(ReportField.ANDROID_VERSION)} <br><br>"
                crashContent += "<b>Configuration</b> <br> <pre>${errorContent.getString(ReportField.CRASH_CONFIGURATION)}</pre> <br><br>"
                crashContent += "<b>Locgat</b> <br> <pre>${errorContent.getString(ReportField.LOGCAT)}</pre>"

                try {
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress("dummyforsend@gmail.com"))
                    message.setRecipient(Message.RecipientType.TO, InternetAddress("h3xlay@gmail.com"))
                    message.subject = contextReference.get()!!.getString(R.string.crash_email_subject)
                    message.setContent(crashContent, "text/html; charset=utf-8")
                    Transport.send(message)
                } catch (e: MessagingException) {
                    e.printStackTrace()
                }
                return null
            }
        }
    }

}