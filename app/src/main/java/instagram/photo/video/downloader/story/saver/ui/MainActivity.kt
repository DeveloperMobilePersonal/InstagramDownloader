package instagram.photo.video.downloader.story.saver.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.data.*
import instagram.photo.video.downloader.story.saver.databinding.ActivityMainBinding
import instagram.photo.video.downloader.story.saver.dialog.DialogDownloader
import instagram.photo.video.downloader.story.saver.dialog.DialogDownloaderListener
import instagram.photo.video.downloader.story.saver.dialog.DialogError
import instagram.photo.video.downloader.story.saver.dialog.DialogLoading
import instagram.photo.video.downloader.story.saver.ex.*
import instagram.photo.video.downloader.story.saver.permission.PermissionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity<ActivityMainBinding>(),
    PermissionManager.PermissionListener,
    DataListener,
    DownloaderListener,
    DialogDownloaderListener {

    val clipManager by inject<ClipManager>()
    val dialogLoading by inject<DialogLoading>()
    val dialogError by inject<DialogError>()
    val dialogDownloader by inject<DialogDownloader>()
    val dataService by inject<DataService> { parametersOf(lifecycleScope) }
    val downloaderService by inject<DownloaderService> { parametersOf(lifecycleScope) }

    private val permissionManager by inject<PermissionManager>()

    override fun loadUI(): Int {
        return R.layout.activity_main
    }

    override fun createUI() {
        addTextChanged()
        onClick()
        lifecycleScope.launch(Dispatchers.IO) {
            val mediaSourceCache = getMediaSourceCache()
            withContext(Dispatchers.Main) {
                if (isActive()) loadCardPreview(mediaSourceCache)
            }
        }
        permissionManager.requestPermissionsReadWriteStorage(this)
    }

    override fun destroyUI() {

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        val url = clipManager.getTextCopy()
        if (hasFocus &&
            dataService.isFocusChanged(url) &&
            permissionManager.isPermissionReadWriteStorage()
        ) {
            fetch(url)
        }
    }

    override fun onPermissionAllow() {

    }

    override fun onPermissionDenied() {

    }

    override fun onPermissionAskAgain() {

    }

    override fun onDataMediaSource(mediaSource: MediaSource) {
        if (!isActive()) return
        dialogLoading.hideUI()
        downloaderService.start(mediaSource.resources.toMutableList())
    }

    override fun onDataError() {
        if (!isActive()) return
        dialogLoading.hideUI()
        dialogError.showUI()
    }

    override fun onDownloadStart() {
        if (!isActive()) return
        dialogDownloader.showUiDownload()
    }

    override fun onDownloadProgress(count: Int, sum: Int) {
        if (!isActive()) return
        val progress = ((count + 1).toDouble() / sum.toDouble()) * 90
        dialogDownloader.updateProgress(progress.toInt())
    }

    override fun onDownloadComplete() {
        if (!isActive()) return
        dialogDownloader.updateProgress(100)
        val mediaSource = dataService.mediaSource
        if (mediaSource != null) {
            lifecycleScope.launch(Dispatchers.IO) {
                val gson = GsonBuilder().disableHtmlEscaping().create()
                putShared(KEY_MEDIA_SOURCE_CACHE, gson.toJson(mediaSource))
            }
        }
    }

    override fun onDownloadError() {
        if (!isActive()) return
        toastShow(R.string.txt_error)
        downloaderService.stop()
        dialogDownloader.hideUI()
    }

    override fun onDialogDownloaderComplete() {
        if (!isActive()) return
        toastShow(R.string.txt_done_download)
        dialogDownloader.hideUI()
        val mediaSource = dataService.mediaSource
        if (mediaSource != null) {
            loadCardPreview(mediaSource)
        }
    }
}