package instagram.photo.video.downloader.story.saver.ads.appopen

interface AdAppOpenApplicationListener {
    fun onAdStartAppOpen(appOpenManager: AppOpenManager){}
    fun onAdShowAppOpen(){}
    fun onAdCloseAppOpen(){}
}