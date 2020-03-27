package hexlay.ums.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkCapabilities
import hexlay.ums.services.events.ConnectedSuccessEvent
import hexlay.ums.services.events.ConnectedUnSuccessEvent
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.connectivityManager

class ConnectivityReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkAvailable(context)) {
            EventBus.getDefault().post(ConnectedSuccessEvent())
        } else {
            EventBus.getDefault().post(ConnectedUnSuccessEvent())
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val network = context.connectivityManager.activeNetwork
        val capabilities = context.connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }
}