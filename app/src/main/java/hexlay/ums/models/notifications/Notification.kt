package hexlay.ums.models.notifications

import com.google.gson.annotations.SerializedName

data class Notification(
    @SerializedName("_id")
    var notificationId: String,
    @SerializedName("notification")
    var notificationData: NotificationData,
    @SerializedName("state")
    var notificationState: String
)