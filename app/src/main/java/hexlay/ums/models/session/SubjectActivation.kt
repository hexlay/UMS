package hexlay.ums.models.session

import com.google.gson.annotations.SerializedName

data class SubjectActivation(
    @SerializedName("subject")
    var subject: CalendarSubject
)