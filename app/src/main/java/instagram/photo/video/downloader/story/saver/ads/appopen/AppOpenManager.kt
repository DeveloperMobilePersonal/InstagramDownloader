package instagram.photo.video.downloader.story.saver.ads.appopen

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

class AppOpenManager(private val context: Context, private val adId: String) {

    companion object {
        const val DEBUG_ID_APP_OPEN_AD = "ca-app-pub-3940256099942544/3419835294"
    }

    private var listener: AppOpenManagerListener? = null
    private var appOpenAd: AppOpenAd? = null
    private var timeAdLoaded = 0L
    private var isShowing = false
    private var startLoadAd = false
    private var timeOutAd = 2 * 60 * 60 * 1000
    private var debug = false
    private var isAutoLoadAd = false
    private var isError = false

    fun setListener(listener: AppOpenManagerListener?) {
        this.listener = listener
    }

    fun setDebugAd(debug: Boolean) {
        this.debug = debug
    }

    fun setTimeOutAd(hours: Int) {
        timeOutAd = hours * 60 * 60 * 1000
    }

    fun setAutoReLoadAd(autoLoadAd: Boolean) {
        isAutoLoadAd = autoLoadAd
    }

    fun isLoadAd(): Boolean {
        return appOpenAd != null
    }

    fun ad(): AppOpenAd? {
        return appOpenAd
    }

    fun isShowing(): Boolean {
        return isShowing
    }

    fun isReadyAd(): Boolean {
        return if (timeAdLoaded == 0L) false else (System.currentTimeMillis() - timeAdLoaded) < timeOutAd
    }

    fun isError(): Boolean {
        return isError
    }

    fun loadAd() {
        if (startLoadAd) return
        if (isLoadAd() && isReadyAd()) {
            listener?.onAdAppOpenLoaded(this)
            return
        }
        clear()
        startLoadAd = true
        AppOpenAd.load(
            context,
            if (debug) DEBUG_ID_APP_OPEN_AD else adId,
            AdRequest.Builder().build(),
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            callback
        )
    }

    fun showAd(activity: Activity) {
        if (isShowing) return
        if (isError()) {
            if (isAutoLoadAd) loadAd()
            listener?.onAdCloseAppOpen()
            return
        }
        if (isLoadAd() && isReadyAd()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    clear()
                    if (isAutoLoadAd) loadAd()
                    listener?.onAdCloseAppOpen()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    clear()
                    isShowing = false
                    listener?.onAdFailToShowAppOpen()
                }

                override fun onAdShowedFullScreenContent() {
                    isShowing = true
                    listener?.onAdShowAppOpen()
                }
            }
            appOpenAd?.show(activity)
        } else {
            if (isAutoLoadAd) loadAd()
            listener?.onAdCloseAppOpen()
        }
    }

    fun clear() {
        appOpenAd = null
        timeAdLoaded = 0L
        isShowing = false
        startLoadAd = false
        isError = false
    }

    private val callback = object : AppOpenAd.AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
            appOpenAd = ad
            isShowing = false
            isError = false
            startLoadAd = false
            timeAdLoaded = System.currentTimeMillis()
            listener?.onAdAppOpenLoaded(this@AppOpenManager)
        }

        override fun onAdFailedToLoad(p0: LoadAdError) {
            appOpenAd = null
            isShowing = false
            isError = true
            startLoadAd = false
            listener?.onAdAppOpenLoadFail(p0.message)
        }
    }
}