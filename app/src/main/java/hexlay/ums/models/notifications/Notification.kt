package hexlay.ums.models.notifications

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    @SerializedName("_id")
    var id: String,
    @SerializedName("notification")
    var data: NotificationData,
    @SerializedName("state")
    var state: String
) : Parcelable