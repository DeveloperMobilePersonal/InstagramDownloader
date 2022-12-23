package instagram.photo.video.downloader.story.saver.dialog

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.BaseAlertDialog
import instagram.photo.video.downloader.story.saver.databinding.AlertRateBinding
import instagram.photo.video.downloader.story.saver.ex.KEY_RATE
import instagram.photo.video.downloader.story.saver.ex.click
import instagram.photo.video.downloader.story.saver.ex.putShared


class DialogRate(private val activity: BaseActivity<*>) :
    BaseAlertDialog<AlertRateBinding>(activity) {

    override fun loadUI(): Int {
        return R.layout.alert_rate
    }

    override fun createUI() {
        viewBinding.btnGrant.click {
            activity.putShared(KEY_RATE, true)
            activity.rate()
            hideUI()
        }
        viewBinding.btClose.click {
            hideUI()
        }
    }

    override fun destroyUI() {

    }

    override fun transparent(): Boolean {
        return true
    }

    override fun width(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    private fun Context.rate() {
        val appPackageName = packageName
        try {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

}