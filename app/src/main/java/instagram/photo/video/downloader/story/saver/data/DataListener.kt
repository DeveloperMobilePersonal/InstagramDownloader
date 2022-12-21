package instagram.photo.video.downloader.story.saver.data

interface DataListener {
    fun onDataMediaSource(mediaSource: MediaSource)
    fun onDataError()
}