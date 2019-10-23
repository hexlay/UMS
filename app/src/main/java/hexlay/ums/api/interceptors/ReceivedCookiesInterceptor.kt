package hexlay.ums.api.interceptors

import android.content.Context
import hexlay.ums.helpers.PreferenceHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ReceivedCookiesInterceptor(private val context: Context) : Interceptor {

    private val preferenceHelper = PreferenceHelper(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if (originalResponse.headers("Set-Cookie").isNotEmpty()) {
            for (header in originalResponse.headers("Set-Cookie")) {
                if (header.startsWith("connect.sid")) {
                    preferenceHelper.connectId = header
                }
            }
        }
        return originalResponse
    }

}