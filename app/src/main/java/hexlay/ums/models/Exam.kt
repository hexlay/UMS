package hexlay.ums.models

import com.google.gson.annotations.SerializedName

data class Exam(
    @SerializedName("subject")
    var subject: String,
    @SerializedName("day")
    var day: String,
    @SerializedName("time")
    var time: String,
    @SerializedName("seat")
    var seat: Int,
    @SerializedName("hall")
    var hall: String,
    @SerializedName("status")
    var status: String,
    @SerializedName("canMove")
    var movable: Boolean
)