package hexlay.ums.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import hexlay.ums.helpers.AppHelper
import hexlay.ums.services.events.ConnectedSuccessEvent
import hexlay.ums.services.events.ConnectedUnSuccessEvent
import org.greenrobot.eventbus.EventBus

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (AppHelper(context).isNetworkAvailable()) {
            EventBus.getDefault().post(ConnectedSuccessEvent())
        } else {
            EventBus.getDefault().post(ConnectedUnSuccessEvent())
        }
    }

}