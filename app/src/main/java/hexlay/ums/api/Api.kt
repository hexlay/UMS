package hexlay.ums.api

import android.content.Context
import com.google.gson.GsonBuilder
import hexlay.ums.api.interceptors.AddCookiesInterceptor
import hexlay.ums.api.interceptors.ConnectionInterceptor
import hexlay.ums.api.interceptors.ReceivedCookiesInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

abstract class Api : UmsAPI {

    companion object {
        @Volatile
        private var INSTANCE: UmsAPI? = null

        fun make(context: Context): UmsAPI {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(Api::class) {
                val cache = Cache(context.cacheDir, 5242880) // 5 * 1024 * 1024
                val client = OkHttpClient.Builder()
                    .addInterceptor(ConnectionInterceptor(context))
                    .addInterceptor(AddCookiesInterceptor(context))
                    .addInterceptor(ReceivedCookiesInterceptor(context))
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
                val instance = retrofit.create(UmsAPI::class.java)
                INSTANCE = instance
                return instance
            }
        }
    }

}