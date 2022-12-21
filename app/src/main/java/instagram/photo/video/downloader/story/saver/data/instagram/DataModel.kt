package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName

class DataModel(
    @SerializedName("shortcode_media")
    val shortcodeMedia: ShortcodeMedia? = null
){
    companion object {
        const val TYPE_SIDE_CAR = "GraphSidecar"
        const val TYPE_GRAPH_IMAGE = "GraphImage"
        const val TYPE_GRAPH_VIDEO = "GraphVideo"
    }
}