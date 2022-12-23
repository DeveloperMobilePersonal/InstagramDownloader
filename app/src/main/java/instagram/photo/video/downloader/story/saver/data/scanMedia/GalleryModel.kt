package instagram.photo.video.downloader.story.saver.data.scanMedia

data class GalleryModel(
    val id: Long,
    val path: String,
    val title: String,
    val displayName: String,
    val duration: String? = null,
    val type: Int = -1
)