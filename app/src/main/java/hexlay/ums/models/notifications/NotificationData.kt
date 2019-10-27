package hexlay.ums.models.notifications

import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("text")
    var notificationDataText: String,
    @SerializedName("title")
    var notificationDataTitle: String,
    @SerializedName("createDateString")
    var notificationDataCreated: String
)