package hexlay.ums.models.session

import com.google.gson.annotations.SerializedName

data class Session(
    @SerializedName("startTime")
    var startTime: String,
    @SerializedName("dayOfWeek")
    var dayOfWeek: Int,
    @SerializedName("room")
    var room: Room,
    @SerializedName("sessionGroup")
    var sessionGroup: SessionGroup
)