package hexlay.ums.models.subject

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("_id")
    var id: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("lecturer")
    var lecturer: String,
    @SerializedName("score")
    var score: Double,
    @SerializedName("fullScore")
    var fullScore: Double,
    @SerializedName("credits")
    var credit: Int,
    @SerializedName("semester")
    var semester: Int,
    @SerializedName("state")
    var semesterState: String,
    @SerializedName("details")
    var details: List<SubjectDetail>
)