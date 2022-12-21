package instagram.photo.video.downloader.story.saver.data

import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.data.instagram.*
import instagram.photo.video.downloader.story.saver.data.instagram.DataModel.Companion.TYPE_GRAPH_IMAGE
import instagram.photo.video.downloader.story.saver.data.instagram.DataModel.Companion.TYPE_GRAPH_VIDEO
import instagram.photo.video.downloader.story.saver.data.instagram.DataModel.Companion.TYPE_SIDE_CAR
import java.util.*

fun DataModel?.exportMediaSource(): MediaSource? {
    if (this == null || shortcodeMedia == null) {
        return null
    }
    val id = shortcodeMedia.id
    val owner = shortcodeMedia.owner
    val caption = shortcodeMedia.edgeMediaToCaption.caption()
    val resources = mutableListOf<MediaSource.ResourceModel>()
    when (shortcodeMedia.typename) {
        TYPE_GRAPH_IMAGE -> {
            getResourcePhoto(shortcodeMedia, id, resources)
        }
        TYPE_GRAPH_VIDEO -> {
            getResourceVideo(shortcodeMedia, id, resources)
        }
        TYPE_SIDE_CAR -> {
            val nodeList = shortcodeMedia.edgeSidecarToChildren?.edgeList
            if (nodeList.isNullOrEmpty()) {
                getResourcePhoto(shortcodeMedia, id, resources)
            } else {
                getResourceSideCar(resources, nodeList)
            }
        }
    }
    return MediaSource(
        id,
        resources,
        caption,
        owner.profilePicUrl(),
        owner.username(),
        owner.fullName()
    )
}

private fun getResourcePhoto(
    shortcodeMedia: ShortcodeMedia,
    id: String,
    resources: MutableList<MediaSource.ResourceModel>
) {
    val src = shortcodeMedia.displayResourceList?.last()?.src.toString()
    val url = src.ifEmpty { shortcodeMedia.displayUrl }
    val resource = MediaSource.ResourceModel(
        id.ifEmpty { UUID.randomUUID().toString() },
        url,
        MediaSource.ResourceModel.Companion.MediaType.PHOTO
    )
    resources.add(resource)
}

private fun getResourceVideo(
    shortcodeMedia: ShortcodeMedia,
    id: String,
    resources: MutableList<MediaSource.ResourceModel>
) {
    var mediaType = MediaSource.ResourceModel.Companion.MediaType.VIDEO
    val src = shortcodeMedia.videoUrl
    val videoUrl = src.ifEmpty {
        mediaType = MediaSource.ResourceModel.Companion.MediaType.PHOTO
        shortcodeMedia.displayUrl
    }
    val resource = MediaSource.ResourceModel(
        id.ifEmpty { UUID.randomUUID().toString() },
        videoUrl,
        mediaType
    )
    resources.add(resource)
}

fun getResourceSideCar(
    resources: MutableList<MediaSource.ResourceModel>,
    nodeList: List<Edge>
) {
    repeat(nodeList.size) {
        val node = nodeList[it].node
        when (node.typename) {
            TYPE_GRAPH_IMAGE -> {
                getResourcePhotoWithNode(node, resources)
            }
            TYPE_GRAPH_VIDEO -> {
                getResourceVideoWithNode(node, resources)
            }
        }
    }
}

private fun getResourcePhotoWithNode(
    node: Node,
    resources: MutableList<MediaSource.ResourceModel>
) {
    val src = node.displayResourceList?.last()?.src.toString()
    val url = src.ifEmpty { node.displayUrl }
    val resource = MediaSource.ResourceModel(
        node.id.ifEmpty { UUID.randomUUID().toString() },
        url,
        MediaSource.ResourceModel.Companion.MediaType.PHOTO
    )
    resources.add(resource)
}

private fun getResourceVideoWithNode(
    node: Node,
    resources: MutableList<MediaSource.ResourceModel>
) {
    val src = node.videoUrl
    val url = src.ifEmpty { node.displayUrl }
    val resource = MediaSource.ResourceModel(
        node.id.ifEmpty { UUID.randomUUID().toString() },
        url,
        MediaSource.ResourceModel.Companion.MediaType.VIDEO
    )
    resources.add(resource)
}