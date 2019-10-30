package hexlay.ums.models.subject

import com.google.gson.annotations.SerializedName

data class SubjectGrade(
    @SerializedName("absolute")
    var maximumScore: Double,
    @SerializedName("relative")
    var relativeScore: Double
)