package hexlay.ums

import android.app.Application
import com.dbflow5.config.DatabaseConfig
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.dbflow5.database.AndroidSQLiteOpenHelper
import com.google.gson.GsonBuilder
import com.jakewharton.threetenabp.AndroidThreeTen
import hexlay.ums.api.UmsAPI
import hexlay.ums.api.interceptors.AddCookiesInterceptor
import hexlay.ums.api.interceptors.ConnectivityInterceptor
import hexlay.ums.api.interceptors.ReceivedCookiesInterceptor
import hexlay.ums.database.UmsDatabase
import hexlay.ums.helpers.acra.EmailSenderFactory
import okhttp3.OkHttpClient
import org.acra.ACRA
import org.acra.annotation.AcraCore
import org.acra.annotation.AcraToast
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

@AcraCore(reportSenderFactoryClasses = [EmailSenderFactory::class])
@AcraToast(resText = R.string.crash_toast_text)
class UMS : Application() {

    private val sqlLiteHelper = AndroidSQLiteOpenHelper.createHelperCreator(this)
    lateinit var umsAPI: UmsAPI
        private set

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        initAPI()
        FlowManager.init(FlowConfig.Builder(this)
            .database(DatabaseConfig.builder(UmsDatabase::class, sqlLiteHelper).databaseName(UmsDatabase.NAME).build())
            .build())
        ACRA.init(this)
    }

    private fun initAPI() {
        val client = OkHttpClient.Builder()
            .addInterceptor(ConnectivityInterceptor(this))
            .addInterceptor(AddCookiesInterceptor(this))
            .addInterceptor(ReceivedCookiesInterceptor(this))
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

    override fun onTerminate() {
        super.onTerminate()
        FlowManager.destroy()
    }

}