package instagram.photo.video.downloader.story.saver.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.ads.appopen.LockAd
import instagram.photo.video.downloader.story.saver.ads.inter.InterShowListener
import instagram.photo.video.downloader.story.saver.app.ContextGlobal
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.data.MediaSource
import instagram.photo.video.downloader.story.saver.ex.*
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryActivity
import instagram.photo.video.downloader.story.saver.ui.preview.PreviewActivity
import instagram.photo.video.downloader.story.saver.unit.ID_MAIN_BANNER
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun MainActivity.openScreenDownloader() {
    viewBinding.lock.show()
    LockAd.set(true)
    interManager.showAd(this, object : InterShowListener {
        override fun onAdInterClose(failToShowAd: Boolean) {
            if (isActive()) {
                LockAd.set(false)
                viewBinding.lock.gone()
                toastShow(R.string.txt_done_download)
                val intent = Intent(this@openScreenDownloader, GalleryActivity::class.java)
                startActivity(intent)
            }
        }

        override fun onAdInterFailShow() {
            if (isActive()) {
                LockAd.set(false)
                viewBinding.lock.gone()
                val intent = Intent(this@openScreenDownloader, GalleryActivity::class.java)
                startActivity(intent)
            }
        }

        override fun onAdInterShow() {
            if (isActive()) {
                viewBinding.lock.show()
                LockAd.set(true)
            }
        }
    })
}

fun MainActivity.loadInterAdmob(){
    LockAd.set(false)
    val countDownloader = getSharedInt(KEY_COUNT_DOWNLOADER, 0)
    val rate = getSharedBoolean(KEY_RATE, false)
    if (rate) {
        if (countDownloader == 1) interManager.loadAd()
    } else {
        if (countDownloader == 1) interManager.loadAd()
    }
}

fun MainActivity.loadBannerAdmob() {
    val adView = AdView(this)
    viewBinding.frameBannerAds.removeAllViews()
    viewBinding.frameBannerAds.addView(adView)
    adView.adUnitId = ID_MAIN_BANNER
    adView.setAdSize(adSize(viewBinding.frameBannerAds))
    adView.loadObserver {
        isErrorBannerAd = !it
        if (isActive() && it.not()) {
            viewBinding.frameBannerAds.gone()
        }
    }
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
}

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
    viewBinding.guide.btGuideVideoNow.click {
        val youtubeID = "V7DdNmBBsE8"
        val intentApp = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$youtubeID"))
        val intentBrowser =
            Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=$youtubeID"))
        try {
            this.startActivity(intentApp)
        } catch (ex: ActivityNotFoundException) {
            kotlin.runCatching {
                this.startActivity(intentBrowser)
            }
        }
    }
}