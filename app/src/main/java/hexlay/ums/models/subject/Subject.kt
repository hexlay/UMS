package hexlay.ums.models.subject

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("name")
    var subjectName: String,
    @SerializedName("lecturer")
    var subjectLecturer: String,
    @SerializedName("score")
    var subjectScore: Double,
    @SerializedName("fullScore")
    var subjectFullScore: Double,
    @SerializedName("credits")
    var subjectCredit: Int,
    @SerializedName("semester")
    var subjectSemester: Int,
    @SerializedName("state")
    var subjectSemesterState: String,
    @SerializedName("details")
    var subjectDetails: List<SubjectDetail>
)