package instagram.photo.video.downloader.story.saver.ui

import androidx.lifecycle.lifecycleScope
import com.google.gson.GsonBuilder
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplication
import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplicationListener
import instagram.photo.video.downloader.story.saver.ads.appopen.AppOpenManager
import instagram.photo.video.downloader.story.saver.ads.inter.InterManager
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.data.*
import instagram.photo.video.downloader.story.saver.databinding.ActivityMainBinding
import instagram.photo.video.downloader.story.saver.dialog.*
import instagram.photo.video.downloader.story.saver.ex.*
import instagram.photo.video.downloader.story.saver.permission.PermissionManager
import instagram.photo.video.downloader.story.saver.permission.RQ_CODE_RESUME_READ_EXTERNAL_STORAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : BaseActivity<ActivityMainBinding>(),
    PermissionManager.PermissionListener,
    DataListener,
    DownloaderListener,
    DialogDownloaderListener,
    AdAppOpenApplicationListener {

    val clipManager by inject<ClipManager>()
    val dialogLoading by inject<DialogLoading>()
    val dialogError by inject<DialogError>()
    val dialogDownloader by inject<DialogDownloader>()
    val dataService by inject<DataService> { parametersOf(lifecycleScope) }
    val downloaderService by inject<DownloaderService> { parametersOf(lifecycleScope) }
    private val dialogPermissionError by inject<DialogPermissionError>()
    private val permissionManager by inject<PermissionManager>()
    private val dialogRate by inject<DialogRate>()
    private val adAppOpenApplication by inject<AdAppOpenApplication>()
    val interManager by inject<InterManager>()
    var isErrorBannerAd = false

    override fun loadUI(): Int {
        return R.layout.activity_main
    }

    override fun createUI() {
        loadBannerAdmob()
        loadInterAdmob()
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

    override fun onResume() {
        adAppOpenApplication.addListener(this)
        super.onResume()
    }

    override fun destroyUI() {
        permissionManager.remove()
        adAppOpenApplication.unregisterLifecycleOwner()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionManager.onRequestPermissionsResult(requestCode)
    }

    override fun onPermissionAllow() {
        if (isActive()) dialogPermissionError.hideUI()
    }

    override fun onPermissionDenied() {
        if (isActive()) {
            dialogPermissionError.listenGrantPermission = {
                if (isActive()) permissionManager.requestPermissionsReadWriteStorage(this)
            }
            dialogPermissionError.showUI()
        }
    }

    override fun onPermissionAskAgain() {
        if (isActive()) {
            dialogPermissionError.listenGrantPermission = {
                permissionManager.openAppSettings(RQ_CODE_RESUME_READ_EXTERNAL_STORAGE)
            }
            dialogPermissionError.showUI()
        }
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
        val countDownloader = getSharedInt(KEY_COUNT_DOWNLOADER, 0)
        if (countDownloader == 1) interManager.loadAd()
        dialogError.hideUI()
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
        viewBinding.edt.setText("")
        dialogDownloader.hideUI()
        val mediaSource = dataService.mediaSource
        if (mediaSource != null) {
            loadCardPreview(mediaSource)
        }
        val rate = getSharedBoolean(KEY_RATE, false)
        val countDownloader = getSharedInt(KEY_COUNT_DOWNLOADER, 0)
        if (countDownloader == 2) {
            putShared(KEY_COUNT_DOWNLOADER, 0)
        } else {
            putShared(KEY_COUNT_DOWNLOADER, countDownloader + 1)
        }
        if (!rate && countDownloader % 2 == 0) {
            dialogRate.showUI()
        } else if (interManager.isLoadAd() && interManager.isReadyAd()) {
            openScreenDownloader()
        } else {
            toastShow(R.string.txt_done_download)
        }
    }

    override fun onAdStartAppOpen(appOpenManager: AppOpenManager) {
        if (!isActive()) return
        if (isErrorBannerAd) {
            viewBinding.frameBannerAds.gone()
        } else {
            viewBinding.frameBannerAds.hide()
        }
        appOpenManager.showAd(this)
    }

    override fun onAdShowAppOpen() {
        if (isActive()) {
            if (isErrorBannerAd) {
                viewBinding.frameBannerAds.gone()
            } else {
                viewBinding.frameBannerAds.hide()
            }
        }
    }

    override fun onAdCloseAppOpen() {
        if (isActive()) {
            if (isErrorBannerAd) {
                viewBinding.frameBannerAds.gone()
            } else {
                viewBinding.frameBannerAds.show()
            }
        }
    }
}