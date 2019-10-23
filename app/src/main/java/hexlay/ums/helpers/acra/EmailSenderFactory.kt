package hexlay.ums.helpers.acra

import android.content.Context
import org.acra.config.CoreConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

class EmailSenderFactory : ReportSenderFactory {

    override fun create(context: Context, config: CoreConfiguration): ReportSender {
        return EmailSender()
    }

    override fun enabled(config: CoreConfiguration): Boolean {
        return true
    }

}