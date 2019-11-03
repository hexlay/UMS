package hexlay.ums

import android.app.Application
import com.dbflow5.config.DatabaseConfig
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.dbflow5.database.AndroidSQLiteOpenHelper
import com.google.gson.GsonBuilder
import com.jakewharton.threetenabp.AndroidThreeTen
import hexlay.ums.api.AccessDeniedException
import hexlay.ums.api.UmsAPI
import hexlay.ums.api.UnauthorizedException
import hexlay.ums.api.interceptors.AddCookiesInterceptor
import hexlay.ums.api.interceptors.ConnectionInterceptor
import hexlay.ums.api.interceptors.ReceivedCookiesInterceptor
import hexlay.ums.database.UmsDatabase
import hexlay.ums.helpers.PreferenceHelper
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.jetbrains.anko.toast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class UMS : Application() {

    private val sqlLiteHelper = AndroidSQLiteOpenHelper.createHelperCreator(this)
    lateinit var umsAPI: UmsAPI
        private set

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initAPI()
        FlowManager.init(
            FlowConfig.Builder(this)
                .database(DatabaseConfig.builder(UmsDatabase::class, sqlLiteHelper).databaseName(UmsDatabase.NAME).build())
                .build()
        )
    }

    private fun initAPI() {
        val cache = Cache(cacheDir, (5 * 1024 * 1024).toLong())
        val client = OkHttpClient.Builder()
            .addInterceptor(ConnectionInterceptor(this))
            .addInterceptor(AddCookiesInterceptor(this))
            .addInterceptor(ReceivedCookiesInterceptor(this))
            .cache(cache)
            .build()
        val gson = GsonBuilder().setLenient().create()
        val retrofit = Retrofit.Builder()
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(UmsAPI.BASE_URL)
            .build()
        umsAPI = retrofit.create(UmsAPI::class.java)
    }

    fun handleError(error: Throwable) {
        if (error is AccessDeniedException || error is UnauthorizedException) {
            PreferenceHelper(baseContext).clearForLogout()
        }
        toast(error.message!!)
    }

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }

}