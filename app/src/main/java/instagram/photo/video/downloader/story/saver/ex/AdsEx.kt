package instagram.photo.video.downloader.story.saver.ex

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

fun AdView.loadObserver(callback: ((Boolean) -> Unit)? = null) {
    adListener = object : AdListener() {
        override fun onAdLoaded() {
            callback?.invoke(true)
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
            callback?.invoke(false)
        }

        override fun onAdOpened() {
            // Code to be executed when an ad opens an overlay that
            // covers the screen.
        }

        override fun onAdClicked() {
            // Code to be executed when the user clicks on an ad.
        }

        override fun onAdClosed() {
            // Code to be executed when the user is about to return
            // to the app after tapping on an ad.
        }
    }
}