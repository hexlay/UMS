package hexlay.ums.models.session

import com.google.gson.annotations.SerializedName

data class SessionGroup(
    @SerializedName("sessionType")
    var sessionType: String,
    @SerializedName("subjectActivation")
    var subjectActivation: SubjectActivation
)