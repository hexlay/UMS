package hexlay.ums.helpers.acra

import android.content.Context
import android.os.AsyncTask
import hexlay.ums.R
import org.acra.ReportField
import org.acra.data.CrashReportData
import org.acra.sender.ReportSender
import org.jetbrains.anko.toast
import java.lang.ref.WeakReference
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage


class EmailSenderExample : ReportSender {

    override fun send(context: Context, errorContent: CrashReportData) {
        SendEmail(context).execute(errorContent)
    }

    companion object {
        class SendEmail(context: Context) : AsyncTask<CrashReportData, Void, Void>() {

            private val contextReference: WeakReference<Context> = WeakReference(context)

            override fun doInBackground(vararg errorContents: CrashReportData): Void? {
                val props = Properties()
                props["mail.smtp.host"] = "smtp.gmail.com"
                props["mail.smtp.socketFactory.port"] = "465"
                props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
                props["mail.smtp.layout_auth"] = "true"
                props["mail.smtp.port"] = "465"

                val session = Session.getDefaultInstance(props, object : javax.mail.Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication("EMAIL", "PASSWORD")
                    }
                })

                val errorContent = errorContents[0]
                var crashContent = ""
                crashContent += "<b>Date:</b> ${errorContent.getString(ReportField.USER_APP_START_DATE)} -> ${errorContent.getString(ReportField.USER_CRASH_DATE)} <br>"
                crashContent += "<b>Phone model:</b> ${errorContent.getString(ReportField.BRAND)} - ${errorContent.getString(ReportField.PHONE_MODEL)} <br>"
                crashContent += "<b>App version:</b> ${errorContent.getString(ReportField.APP_VERSION_NAME)} - ${errorContent.getString(ReportField.APP_VERSION_CODE)} <br>"
                crashContent += "<b>Android version:</b> ${errorContent.getString(ReportField.ANDROID_VERSION)} <br><br>"
                crashContent += "<b>Configuration</b> <br> <pre id='json'>${errorContent.getString(ReportField.CRASH_CONFIGURATION)}</pre> <br><br>"
                crashContent += "<b>Locgat</b> <br> <pre>${errorContent.getString(ReportField.LOGCAT)}</pre>"

                try {
                    val message = MimeMessage(session)
                    message.setFrom(InternetAddress("EMAIL"))
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("EMAIL"))
                    message.subject = contextReference.get()!!.getString(R.string.crash_email_subject)
                    message.setContent(crashContent, "text/html; charset=utf-8")
                    Transport.send(message)
                } catch (e: MessagingException) {
                    contextReference.get()!!.toast("Error sending email")
                }
                return null
            }
        }
    }

}