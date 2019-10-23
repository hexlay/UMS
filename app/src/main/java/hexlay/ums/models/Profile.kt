package hexlay.ums.models

import com.dbflow5.annotation.PrimaryKey
import com.dbflow5.annotation.Table
import com.google.gson.annotations.SerializedName
import hexlay.ums.database.UmsDatabase

@Table(database = UmsDatabase::class, allFields = true)
data class Profile(
    @SerializedName("firstName")
    var firstName: String? = null,
    @SerializedName("lastName")
    var lastName: String? = null,
    @SerializedName("email")
    var email: String? = null,
    @PrimaryKey
    @SerializedName("personalNo")
    var personalNumber: String? = null,
    @SerializedName("phone")
    var phoneNumber: String? = null,
    @SerializedName("photo")
    var photoUrl: String? = null,
    @SerializedName("gender")
    var gender: String? = null
)