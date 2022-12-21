package instagram.photo.video.downloader.story.saver.data

interface DownloaderListener {
    fun onDownloadStart()
    fun onDownloadProgress(count: Int, sum: Int)
    fun onDownloadComplete()
    fun onDownloadError()
}