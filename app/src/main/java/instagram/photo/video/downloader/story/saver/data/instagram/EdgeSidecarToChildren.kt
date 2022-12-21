package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName
import instagram.photo.video.downloader.story.saver.data.instagram.Edge

data class EdgeSidecarToChildren(
    @SerializedName("edges")
    val edgeList: List<Edge>? = null
)