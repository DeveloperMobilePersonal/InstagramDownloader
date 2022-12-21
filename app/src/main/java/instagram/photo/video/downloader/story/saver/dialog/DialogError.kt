package instagram.photo.video.downloader.story.saver.dialog

import android.view.ViewGroup
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.BaseAlertDialog
import instagram.photo.video.downloader.story.saver.databinding.ErrorAlertLayoutBinding
import instagram.photo.video.downloader.story.saver.ex.click


class DialogError(activity: BaseActivity<*>) :
    BaseAlertDialog<ErrorAlertLayoutBinding>(activity) {
    override fun loadUI(): Int {
        return R.layout.error_alert_layout
    }

    override fun createUI() {
        viewBinding.btnClose.click {
            hideUI()
        }
    }

    override fun destroyUI() {

    }

    override fun width(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    override fun transparent(): Boolean {
        return true
    }

    override fun animator(): Int {
        return R.style.AnimatorScale
    }
}