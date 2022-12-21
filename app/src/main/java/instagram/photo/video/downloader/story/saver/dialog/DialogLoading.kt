package instagram.photo.video.downloader.story.saver.dialog

import android.view.ViewGroup
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.BaseAlertDialog
import instagram.photo.video.downloader.story.saver.databinding.LoadingAlertLayoutBinding

class DialogLoading(activity: BaseActivity<*>) :
    BaseAlertDialog<LoadingAlertLayoutBinding>(activity) {
    override fun loadUI(): Int {
        return R.layout.loading_alert_layout
    }

    override fun createUI() {

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