package hexlay.ums.models.subject

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubjectGrade(
    @SerializedName("absolute")
    var gradeMax: Int,
    @SerializedName("relative")
    var gradeValue: Int
) : Parcelable