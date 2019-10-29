package hexlay.ums.models

import com.google.gson.annotations.SerializedName

data class Exam(
    @SerializedName("subject")
    var examSubject: String,
    @SerializedName("day")
    var examDay: String,
    @SerializedName("time")
    var examTime: String,
    @SerializedName("seat")
    var examSeat: Int,
    @SerializedName("hall")
    var examHall: String,
    @SerializedName("status")
    var examStatus: String,
    @SerializedName("canMove")
    var examMovable: Boolean
)