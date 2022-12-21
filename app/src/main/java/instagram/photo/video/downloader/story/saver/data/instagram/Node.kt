package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName
import instagram.photo.video.downloader.story.saver.data.instagram.InfoResource

data class Node(
    @SerializedName("__typename")
    val typename: String = "",

    @SerializedName("id")
    val id: String = "",

    @SerializedName("display_url")
    val displayUrl: String = "",

    @SerializedName("display_resources")
    val displayResourceList: List<InfoResource>? = null,

    @SerializedName("accessibility_caption")
    val caption: String = "",

    @SerializedName("is_video")
    val isVideo: Boolean = false,

    @SerializedName("video_url")
    val videoUrl: String = ""

)