package instagram.photo.video.downloader.story.saver.ads.inter

interface InterShowListener {
   fun onAdInterClose(failToShowAd:Boolean)
   fun onAdInterFailShow()
   fun onAdInterShow()
}