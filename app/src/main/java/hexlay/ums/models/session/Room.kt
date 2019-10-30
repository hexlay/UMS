package hexlay.ums.models.session

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("name")
    var name: String,
    @SerializedName("building")
    var building: String
)