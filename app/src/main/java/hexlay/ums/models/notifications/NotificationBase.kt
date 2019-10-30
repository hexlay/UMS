package hexlay.ums.models.notifications

import com.google.gson.annotations.SerializedName

data class NotificationBase(
    @SerializedName("docs")
    var notifications: List<Notification>,
    @SerializedName("pages")
    var maxPages: Int,
    @SerializedName("page")
    var currentPage: Int
)