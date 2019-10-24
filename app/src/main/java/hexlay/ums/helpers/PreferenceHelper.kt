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
        get() = settings.getInt("ums_dark_mode", 1)
        set(value) {
            settings.edit {
                putInt("ums_dark_mode", value)
            }
        }

    fun clear() {
        settings.edit {
            clear()
        }
    }

}