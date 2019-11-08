package hexlay.ums.models.notifications

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class NotificationData(
    @SerializedName("text")
    var text: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("createDateString")
    var createdDate: String
) : Parcelable