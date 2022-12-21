package instagram.photo.video.downloader.story.saver.data

data class MediaSource(
    var id: String = "",
    var resources: List<ResourceModel> = emptyList(),
    var caption: String = "",
    val profilePicUrl: String = "",
    var username: String = "",
    val fullName: String = ""
) {
    data class ResourceModel(
        val id: String,
        val url: String = "",
        val mediaType: MediaType = MediaType.PHOTO
    ) {
        companion object {
            enum class MediaType {
                VIDEO,
                PHOTO
            }
        }
    }
}