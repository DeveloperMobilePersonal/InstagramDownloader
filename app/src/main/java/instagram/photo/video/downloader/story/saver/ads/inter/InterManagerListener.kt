package instagram.photo.video.downloader.story.saver.ads.inter

interface InterManagerListener {
    fun onAdInterLoaded(interManager: InterManager){}
    fun onAdInterLoadFail(message: String){}
    fun onAdInterClose(failToShowAd:Boolean){}
    fun onAdInterFailShow(){}
    fun onAdInterShow(){}
}