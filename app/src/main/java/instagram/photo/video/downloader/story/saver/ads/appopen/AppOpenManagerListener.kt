package instagram.photo.video.downloader.story.saver.ads.appopen

interface AppOpenManagerListener {
    fun onAdAppOpenLoaded(appOpenManagerListener: AppOpenManager) {}
    fun onAdAppOpenLoadFail(message: String) {}
    fun onAdShowAppOpen() {}
    fun onAdFailToShowAppOpen() {}
    fun onAdCloseAppOpen() {}
}