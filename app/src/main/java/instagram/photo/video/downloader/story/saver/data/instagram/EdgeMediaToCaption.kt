package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName

fun EdgeMediaToCaption?.caption(): String {
    if (this == null) return ""
    return if (captionList.isNullOrEmpty()) {
        ""
    } else {
        captionList.first().node?.caption.toString()
    }
}

class EdgeMediaToCaption {

    @SerializedName("edges")
    val captionList: List<Caption>? = null

    data class Caption(
        @SerializedName("node")
        val node: Node? = null
    )

    data class Node(
        @SerializedName("text")
        val caption: String? = ""
    )
}