package hexlay.ums.database

import com.dbflow5.annotation.ConflictAction
import com.dbflow5.annotation.Database
import com.dbflow5.config.DBFlowDatabase

@Database(version = UmsDatabase.VERSION, backupEnabled = true, insertConflict = ConflictAction.REPLACE)
abstract class UmsDatabase : DBFlowDatabase() {
    companion object {
        const val NAME = "db_ums"
        const val VERSION = 3
    }
}