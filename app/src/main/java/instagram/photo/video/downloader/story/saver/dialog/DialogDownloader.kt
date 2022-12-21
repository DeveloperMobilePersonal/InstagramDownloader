package instagram.photo.video.downloader.story.saver.dialog

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.BaseAlertDialog
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.databinding.DownloaderAlertLayoutBinding

class DialogDownloader(
    activity: BaseActivity<*>,
    private val listener: DialogDownloaderListener? = null
) :
    BaseAlertDialog<DownloaderAlertLayoutBinding>(activity) {

    private val handler by lazy { Handler(Looper.getMainLooper()) }
    private var progress = 0
    private var mProgress = 0

    override fun loadUI(): Int {
        return R.layout.downloader_alert_layout
    }

    override fun createUI() {

    }

    override fun destroyUI() {
        handler.removeCallbacksAndMessages(null)
    }

    override fun transparent(): Boolean {
        return true
    }

    override fun width(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    @SuppressLint("SetTextI18n")
    private val runnable = Runnable {
        kotlin.runCatching {
            if (hasShowUI()) {
                if (mProgress < progress) {
                    mProgress++
                } else if (mProgress >= 99) {
                    mProgress = 100
                }
                kotlin.runCatching {
                    viewBinding.tvProgressText.text = "$mProgress%"
                    viewBinding.progressBar.setProgressCompat(mProgress, true)
                }
                if (mProgress < 100) {
                    val delayMillis = if (mProgress < 20) {
                        60L
                    } else if (mProgress < 80) {
                        20L
                    } else 60L
                    post(delayMillis)
                } else {
                    listener?.onDialogDownloaderComplete()
                }
            }
        }
    }

    private fun post(delayMillis: Long) {
        handler.postDelayed(runnable, delayMillis)
    }

    @SuppressLint("SetTextI18n")
    fun showUiDownload() {
        showUI()
        progress = 0
        mProgress = 0
        handler.removeCallbacksAndMessages(null)
        viewBinding.tvProgressText.text = "0%"
        viewBinding.progressBar.progress = 0
        post(100)
    }

    fun updateProgress(progress: Int) {
        this.progress = progress
    }
}