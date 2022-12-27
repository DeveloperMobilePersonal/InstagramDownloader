package instagram.photo.video.downloader.story.saver.ads.appopen

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ProcessLifecycleOwner
import instagram.photo.video.downloader.story.saver.ex.KEY_DIAMOND
import instagram.photo.video.downloader.story.saver.ex.getSharedBoolean

class AdAppOpenApplication(private val context: Context, private val adId: String) :
    LifecycleObserver, AppOpenManagerListener {

    private val appOpenManager: AppOpenManager by lazy {
        AppOpenManager(context, adId)
    }
    private var listener: AdAppOpenApplicationListener? = null

    fun addListener(listener: AdAppOpenApplicationListener?) {
        this.listener = listener
    }

    fun registerLifecycleOwner() {
        appOpenManager.setAutoReLoadAd(true)
        appOpenManager.setListener(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(lifecycleEventObserver)
    }

    fun unregisterLifecycleOwner() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(lifecycleEventObserver)
    }

    private val lifecycleEventObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_START && !context.getSharedBoolean(KEY_DIAMOND, false)) {
            if (appOpenManager.isLoadAd() && appOpenManager.isReadyAd()) {
                if (!appOpenManager.isShowing() && !LockAd.lockAd) {
                    listener?.onAdStartAppOpen(appOpenManager)
                }
            } else {
                appOpenManager.loadAd()
            }
        }
    }

    override fun onAdShowAppOpen() {
        listener?.onAdShowAppOpen()
    }

    override fun onAdCloseAppOpen() {
        listener?.onAdCloseAppOpen()
    }

    override fun onAdFailToShowAppOpen() {
        listener?.onAdCloseAppOpen()
    }
}