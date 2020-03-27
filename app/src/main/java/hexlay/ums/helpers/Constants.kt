package hexlay.ums.helpers

import android.os.Build

object Constants {

    val isPie: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.P

    val isNougat: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

}