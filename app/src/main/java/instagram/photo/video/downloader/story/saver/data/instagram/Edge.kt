package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName

data class Edge(
    @SerializedName("node")
    val node: Node = Node()
)