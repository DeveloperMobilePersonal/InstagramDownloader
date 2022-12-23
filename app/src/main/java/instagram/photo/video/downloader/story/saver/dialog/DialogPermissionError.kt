package instagram.photo.video.downloader.story.saver.dialog

import android.view.ViewGroup
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.BaseAlertDialog
import instagram.photo.video.downloader.story.saver.databinding.AlertPermissionErrorBinding
import instagram.photo.video.downloader.story.saver.ex.click

class DialogPermissionError(activity: BaseActivity<*>) :
    BaseAlertDialog<AlertPermissionErrorBinding>(activity) {

    var listenGrantPermission: (() -> Unit)? = null

    override fun loadUI(): Int {
        return R.layout.alert_permission_error
    }

    override fun createUI() {
        viewBinding.btnGrant.click {
            listenGrantPermission?.let { it() }
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

}