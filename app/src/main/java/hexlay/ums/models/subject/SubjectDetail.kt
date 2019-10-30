package hexlay.ums.models.subject

import com.google.gson.annotations.SerializedName

data class SubjectDetail(
    @SerializedName("name")
    var name: String,
    @SerializedName("type")
    var type: String,
    @SerializedName("minValue")
    var minimalScore: Double,
    @SerializedName("absolute")
    var maximalScore: Double,
    @SerializedName("grade")
    var grade: SubjectGrade
)