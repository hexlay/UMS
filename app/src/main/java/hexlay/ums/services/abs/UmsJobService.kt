package hexlay.ums.services.abs

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import com.google.gson.GsonBuilder
import hexlay.ums.api.AccessDeniedException
import hexlay.ums.api.UmsAPI
import hexlay.ums.api.UnauthorizedException
import hexlay.ums.api.interceptors.AddCookiesInterceptor
import hexlay.ums.api.interceptors.ConnectionInterceptor
import hexlay.ums.api.interceptors.ReceivedCookiesInterceptor
import hexlay.ums.helpers.PreferenceHelper
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


abstract class UmsJobService : JobService() {

    protected lateinit var preferenceHelper: PreferenceHelper
    protected lateinit var umsAPI: UmsAPI
    protected abstract var channelId: String

    override fun onCreate() {
        super.onCreate()
        val client = OkHttpClient.Builder()
            .addInterceptor(ConnectionInterceptor(applicationContext))
            .addInterceptor(AddCookiesInterceptor(applicationContext))
            .addInterceptor(ReceivedCookiesInterceptor(applicationContext))
            .build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(UmsAPI.BASE_URL)
            .build()
        preferenceHelper = PreferenceHelper(applicationContext)
        umsAPI = retrofit.create(UmsAPI::class.java)
    }

    override fun onStartJob(jobParameters: JobParameters): Boolean {
        sync()
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        return false
    }

    protected fun handleError(error: Throwable) {
        if (error is AccessDeniedException || error is UnauthorizedException) {
            preferenceHelper.clearForLogout()
        }
    }

    protected fun createNotificationChannel(name: String, description: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = description
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    protected fun generateId(id: String): Int {
        val regex = Regex("(\\d+)")
        val numbers = regex.findAll(id)
        val timeRand = (System.currentTimeMillis() % Integer.MAX_VALUE).toInt()
        return timeRand + numbers.map { it.value }.joinToString(separator = "").take(4).toInt()
    }

    abstract fun sync()

}