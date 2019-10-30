package hexlay.ums.models.notifications

import com.google.gson.annotations.SerializedName

data class NotificationData(
    @SerializedName("text")
    var text: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("createDateString")
    var createdDate: String
)