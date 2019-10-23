package hexlay.ums.models.subject

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("name")
    var subjectName: String,
    @SerializedName("lecturer")
    var subjectLecturer: String,
    @SerializedName("score")
    var subjectScore: Int,
    @SerializedName("credits")
    var subjectCredit: Int,
    @SerializedName("semester")
    var subjectSemester: Int,
    @SerializedName("details")
    var subjectDetails: List<SubjectDetail>
)