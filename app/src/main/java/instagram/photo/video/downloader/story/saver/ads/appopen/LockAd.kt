package instagram.photo.video.downloader.story.saver.ads.appopen

object LockAd {
    var lockAd = false
    fun set(lockAd: Boolean) {
        LockAd.lockAd = lockAd
    }
}