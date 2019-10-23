package hexlay.ums.api.interceptors

import android.content.Context
import android.net.NetworkCapabilities
import hexlay.ums.exceptions.NoConnectivityException
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.anko.connectivityManager
import java.io.IOException

class ConnectivityInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isNetworkAvailable()) {
            throw NoConnectivityException()
        }
        val builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private fun isNetworkAvailable(): Boolean {
        val network = context.connectivityManager.activeNetwork
        val capabilities = context.connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

}