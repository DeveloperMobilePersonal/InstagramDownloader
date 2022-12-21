package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName

fun Owner?.profilePicUrl(): String {
    if (this == null) return ""
    return if (profilePicUrl.isNullOrEmpty()) "" else profilePicUrl
}

fun Owner?.username(): String {
    if (this == null) return ""
    return if (username.isNullOrEmpty()) "" else username
}

fun Owner?.fullName(): String {
    if (this == null) return ""
    return if (fullName.isNullOrEmpty()) "" else fullName
}

data class Owner(
    @SerializedName("id")
    val id: String = "",

    @SerializedName("is_verified")
    val isVerified: Boolean = false,

    @SerializedName("is_private")
    val isPrivate: Boolean = false,

    @SerializedName("profile_pic_url")
    val profilePicUrl: String? = "",

    @SerializedName("username")
    val username: String? = "",

    @SerializedName("full_name")
    val fullName: String? = "",
)