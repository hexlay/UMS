package hexlay.ums.api.interceptors

import android.content.Context
import hexlay.ums.helpers.PreferenceHelper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AddCookiesInterceptor(private val context: Context) : Interceptor {

    private val preferenceHelper = PreferenceHelper(context)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        builder.addHeader("Cookie", preferenceHelper.connectId)
        return chain.proceed(builder.build())
    }

}