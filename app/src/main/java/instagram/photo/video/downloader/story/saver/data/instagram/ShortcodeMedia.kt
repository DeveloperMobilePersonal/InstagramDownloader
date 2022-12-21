package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName
import instagram.photo.video.downloader.story.saver.data.instagram.EdgeMediaToCaption
import instagram.photo.video.downloader.story.saver.data.instagram.EdgeSidecarToChildren
import instagram.photo.video.downloader.story.saver.data.instagram.InfoResource
import instagram.photo.video.downloader.story.saver.data.instagram.Owner

class ShortcodeMedia(

    @SerializedName("__typename")
    val typename: String = "",

    @SerializedName("id")
    val id: String = "",

    @SerializedName("display_url")
    val displayUrl: String = "",

    @SerializedName("display_resources")
    val displayResourceList: List<InfoResource>? = null,

    @SerializedName("is_video")
    val isVideo: Boolean = false,

    @SerializedName("video_url")
    val videoUrl: String = "",

    @SerializedName("owner")
    val owner: Owner? = null,

    @SerializedName("edge_sidecar_to_children")
    val edgeSidecarToChildren: EdgeSidecarToChildren? = null,

    @SerializedName("edge_media_to_caption")
    val edgeMediaToCaption: EdgeMediaToCaption? = null

)