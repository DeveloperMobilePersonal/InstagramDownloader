package instagram.photo.video.downloader.story.saver.ads.inter

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import instagram.photo.video.downloader.story.saver.ex.KEY_DIAMOND
import instagram.photo.video.downloader.story.saver.ex.getSharedBoolean

/**
<!-- Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713 -->
<meta-data
android:name="com.google.android.gms.ads.APPLICATION_ID"
android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
 * */
class InterManager(private val context: Context, private val adId: String) {

    companion object {
        const val DEBUG_ID_INTERSTITIAL_AD = "ca-app-pub-3940256099942544/1033173712"
    }

    private var mInterstitialAd: InterstitialAd? = null
    private var interLoadListener: InterManagerListener? = null
    private var timeAdLoaded = 0L
    private var isShowing = false
    private var isError = false
    private var timeOutAd = 2 * 60 * 60 * 1000
    private var isAutoLoadAd = false
    private var startLoadAd = false
    private var debug = false

    fun setDebugAd(debug: Boolean) {
        this.debug = debug
    }

    fun setTimeOutAd(hours: Int) {
        timeOutAd = hours * 60 * 60 * 1000
    }

    fun setAutoReLoadAd(autoLoadAd: Boolean) {
        isAutoLoadAd = autoLoadAd
    }

    fun setLoadListenerAd(listener: InterManagerListener?) {
        this.interLoadListener = listener
    }

    fun isLoadAd(): Boolean {
        return mInterstitialAd != null
    }

    fun isReadyAd(): Boolean {
        return if (timeAdLoaded == 0L) false else (System.currentTimeMillis() - timeAdLoaded) < timeOutAd
    }

    fun isError(): Boolean {
        return isError
    }

    fun clean() {
        mInterstitialAd = null
    }

    fun loadAd() {
        if (context.getSharedBoolean(KEY_DIAMOND, false)) return
        if (startLoadAd) return
        if (isLoadAd() && isReadyAd()) {
            interLoadListener?.onAdInterLoaded(this@InterManager)
            return
        }
        clear()
        startLoadAd = true
        InterstitialAd.load(
            context,
            if (debug) DEBUG_ID_INTERSTITIAL_AD else adId,
            AdRequest.Builder().build(), callback
        )
    }

    fun showAd(activity: Activity, listener: InterShowListener? = null) {
        if (context.getSharedBoolean(KEY_DIAMOND, false)) {
            listener?.onAdInterClose(false)
            return
        }
        if (isShowing) return
        if (isError()) {
            if (isAutoLoadAd) loadAd()
            listener?.onAdInterClose(true)
            interLoadListener?.onAdInterClose(true)
            return
        }
        if (isLoadAd() && isReadyAd()) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    clear()
                    if (isAutoLoadAd) loadAd()
                    listener?.onAdInterClose(false)
                    interLoadListener?.onAdInterClose(false)
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    isShowing = false
                    listener?.onAdInterFailShow()
                    interLoadListener?.onAdInterFailShow()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowing = true
                    listener?.onAdInterShow()
                    interLoadListener?.onAdInterShow()
                }
            }
            mInterstitialAd?.show(activity)
        } else {
            if (isAutoLoadAd) loadAd()
            listener?.onAdInterClose(true)
            interLoadListener?.onAdInterClose(true)
        }
    }

    fun isShowingAd(): Boolean {
        return isShowing
    }

    fun clear() {
        mInterstitialAd = null
        timeAdLoaded = 0L
        isShowing = false
        startLoadAd = false
        isError = false
    }

    private val callback = object : InterstitialAdLoadCallback() {
        override fun onAdLoaded(ad: InterstitialAd) {
            mInterstitialAd = ad
            isShowing = false
            isError = false
            startLoadAd = false
            timeAdLoaded = System.currentTimeMillis()
            interLoadListener?.onAdInterLoaded(this@InterManager)
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
            mInterstitialAd = null
            isShowing = false
            isError = true
            startLoadAd = false
            interLoadListener?.onAdInterLoadFail(p0.message)
        }
    }
}