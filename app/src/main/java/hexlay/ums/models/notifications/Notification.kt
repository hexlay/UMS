package hexlay.ums.models.notifications

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id")
    var id: String,
    @SerializedName("notification")
    var data: NotificationData,
    @SerializedName("state")
    var state: String
)