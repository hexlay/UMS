package hexlay.ums.models.subject

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubjectDetail(
    @SerializedName("name")
    var detailName: String,
    @SerializedName("type")
    var detailType: String,
    @SerializedName("minValue")
    var detailMinScore: Int,
    @SerializedName("absolute")
    var detailMaxScore: Int,
    @SerializedName("grade")
    var detailGrade: SubjectGrade
) : Parcelable