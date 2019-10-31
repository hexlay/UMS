package hexlay.ums.helpers

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class PreferenceHelper(context: Context) {

    private var settings: SharedPreferences = context.getSharedPreferences("ums_preferences", Context.MODE_PRIVATE)

    var connectId: String
        get() = settings.getString("ums_cn_id", "")!!
        set(value) {
            settings.edit {
                putString("ums_cn_id", value)
            }
        }

    var darkMode: Int
        get() = settings.getInt("ums_dark_mode", 0)
        set(value) {
            settings.edit {
                putInt("ums_dark_mode", value)
            }
        }

    var getNotifications: Boolean
        get() = settings.getBoolean("ums_get_notis", true)
        set(value) {
            settings.edit {
                putBoolean("ums_get_notis", value)
            }
        }

    var passwordHash: String
        get() = settings.getString("ums_pwd_hash", "")!!
        set(value) {
            settings.edit {
                putString("ums_pwd_hash", value)
            }
        }

    var lastNotificationId: String
        get() = settings.getString("ums_last_noti_id", "0")!!
        set(value) {
            settings.edit {
                putString("ums_last_noti_id", value)
            }
        }

    fun clearForLogout() {
        connectId = ""
        passwordHash = ""
    }

}