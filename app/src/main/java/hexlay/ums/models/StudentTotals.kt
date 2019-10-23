package hexlay.ums.models

import com.google.gson.annotations.SerializedName

data class StudentTotals(
    @SerializedName("lastSemesterAverage")
    var lastAverage: Double,
    @SerializedName("average")
    var currentAverage: Double,
    @SerializedName("gpa")
    var currentGpa: Double
)