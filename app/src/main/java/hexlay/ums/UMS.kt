package hexlay.ums

import android.app.Application
import com.dbflow5.config.DatabaseConfig
import com.dbflow5.config.FlowConfig
import com.dbflow5.config.FlowManager
import com.dbflow5.database.AndroidSQLiteOpenHelper
import com.jakewharton.threetenabp.AndroidThreeTen
import hexlay.ums.database.UmsDatabase

class UMS : Application() {

    private val sqlLiteHelper = AndroidSQLiteOpenHelper.createHelperCreator(this)

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        FlowManager.init(
            FlowConfig.Builder(this)
                .database(DatabaseConfig.builder(UmsDatabase::class, sqlLiteHelper).databaseName(UmsDatabase.NAME).build())
                .build()
        )
    }

}