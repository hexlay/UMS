package hexlay.ums

import android.app.Application
import com.dbflow5.config.DatabaseConfig
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.dbflow5.database.AndroidSQLiteOpenHelper
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.crashreporter.CrashReporterPlugin
import com.facebook.flipper.plugins.databases.DatabasesFlipperPlugin
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
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
import hexlay.ums.services.events.LogoutEvent
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.greenrobot.eventbus.EventBus
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
        initAPI()
        AndroidThreeTen.init(this)
        SoLoader.init(this, false)
        FlowManager.init(
            FlowConfig.Builder(this)
                .database(DatabaseConfig.builder(UmsDatabase::class, sqlLiteHelper).databaseName(UmsDatabase.NAME).build())
                .build()
        )
        if (BuildConfig.DEBUG && FlipperUtils.shouldEnableFlipper(this)) {
            val client = AndroidFlipperClient.getInstance(this)
            client.addPlugin(InspectorFlipperPlugin(this, DescriptorMapping.withDefaults()))
            client.addPlugin(CrashReporterPlugin.getInstance())
            client.addPlugin(DatabasesFlipperPlugin(this))
            client.addPlugin(SharedPreferencesFlipperPlugin(this, "ums_preferences"))
            client.start()
        }
    }

    private fun initAPI() {
        val cache = Cache(cacheDir, 5242880) // 5 * 1024 * 1024
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
            EventBus.getDefault().post(LogoutEvent())
        }
        toast(error.message!!)
    }

}