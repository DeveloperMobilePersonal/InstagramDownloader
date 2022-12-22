package instagram.photo.video.downloader.story.saver.ui

import android.content.Intent
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.app.ContextGlobal
import instagram.photo.video.downloader.story.saver.data.MediaSource
import instagram.photo.video.downloader.story.saver.ex.*
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MainActivity.loadCardPreview(mediaSourceCache: MediaSource? = null) {
    if (!isActive()) return
    if (mediaSourceCache == null ||
        mediaSourceCache.profilePicUrl.isEmpty() ||
        mediaSourceCache.username.isEmpty() ||
        mediaSourceCache.resources.isEmpty() ||
        mediaSourceCache.id.isEmpty()
    ) {
        viewBinding.crPreview.gone()
    } else {
        viewBinding.crPreview.show()
        viewBinding.ivPreview.load(
            mediaSourceCache.resources.first().url,
            viewBinding.progressBarAvatar
        )
        viewBinding.tvTitle.text = mediaSourceCache.username
        viewBinding.tvMessage.text = mediaSourceCache.caption
    }
}

fun MainActivity.fetch(url: String) {
    ContextGlobal.get().putShared(KEY_URL_CACHE, url)
    val urlModel = url.getUrlRelease()
    viewBinding.edt.setText(url)
    if (urlModel == null) {
        toastShow(R.string.txt_url_not_illegal)
    } else {
        dialogLoading.showUI()
        dataService.fetch(urlModel)
    if (urlModel.isStory) {
            dataService.fetchStory(urlModel)
        } else {
            dataService.fetch(urlModel)
        }
    }
}

fun MainActivity.addTextChanged() {
    viewBinding.btStartDownload.alpha = 0.5f
    viewBinding.edt.addTextChangedListener {
        if (it.toString().trim().isEmpty()) {
            viewBinding.btCleanEdt.hide()
            viewBinding.btStartDownload.alpha = 0.5f
        } else {
            viewBinding.btCleanEdt.show()
            viewBinding.btStartDownload.alpha = 1f
        }
    }
}

fun MainActivity.onClick() {
    viewBinding.btCleanEdt.click {
        viewBinding.edt.setText("")
    }
    viewBinding.btPaste.click {
        fetch(clipManager.getTextCopy())
    }
    viewBinding.btStartDownload.click {
        fetch(viewBinding.edt.text.toString())
    }
    viewBinding.btDownload.click {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
    viewBinding.crPreview.click {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
    viewBinding.btWatchNow.click {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }
    viewBinding.btInstagram.click {
        kotlin.runCatching {
            val launchIntent =
                packageManager.getLaunchIntentForPackage("com.instagram.android")
            startActivity(launchIntent)
        }.getOrElse {
            toastShow(R.string.txt_not_instagram)
        }
    }
}