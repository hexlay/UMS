package hexlay.ums.api.interceptors

import android.content.Context
import android.net.NetworkCapabilities
import hexlay.ums.api.AccessDeniedException
import hexlay.ums.api.NotFoundException
import hexlay.ums.api.UnauthorizedException
import okhttp3.Interceptor
import okhttp3.Response
import org.jetbrains.anko.connectivityManager

class ConnectionInterceptor(private val context: Context) : Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = if (isNetworkAvailable()) {
            request.newBuilder().build()
        } else {
            //val sevenDay = 60 * 60 * 24 * 7
            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=604800").build()
        }
        val response = chain.proceed(request)
        when (response.code) {
            401 -> throw UnauthorizedException()
            403 -> throw AccessDeniedException()
            404 -> throw NotFoundException()
        }
        return response
    }

    private fun isNetworkAvailable(): Boolean {
        val network = context.connectivityManager.activeNetwork
        val capabilities = context.connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
    }

}