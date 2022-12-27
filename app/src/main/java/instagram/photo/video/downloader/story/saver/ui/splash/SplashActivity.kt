package instagram.photo.video.downloader.story.saver.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.lifecycle.lifecycleScope
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplication
import instagram.photo.video.downloader.story.saver.ads.inter.InterManager
import instagram.photo.video.downloader.story.saver.ads.inter.InterManagerListener
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.databinding.ActivitySplashBinding
import instagram.photo.video.downloader.story.saver.ex.hide
import instagram.photo.video.downloader.story.saver.ex.load
import instagram.photo.video.downloader.story.saver.ui.MainActivity
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity<ActivitySplashBinding>(),
    JobSplash.JobProgress,
    InterManagerListener {

    private val jobSplash by inject<JobSplash>()
    private val interManager by inject<InterManager>()
    private val adAppOpenApplication by inject<AdAppOpenApplication>()

    override fun loadAd() {
        interManager.loadAd()
        interManager.setLoadListenerAd(this)
        adAppOpenApplication.addListener(null)
    }

    override fun loadUI(): Int {
        return R.layout.activity_splash
    }

    override fun createUI() {
        adAppOpenApplication.registerLifecycleOwner()
        viewBinding.appCompatImageView.load(R.drawable.app_icon, Color.TRANSPARENT)
        loadAd()
    }

    override fun destroyUI() {
        jobSplash.stopJob()
    }

    override fun onResume() {
        jobSplash.startJob(lifecycleScope, this)
        super.onResume()
    }

    override fun onPause() {
        jobSplash.stopJob()
        super.onPause()
    }

    override fun onBackPressed() {

    }

    override fun onAdInterClose(failToShowAd: Boolean) {
        if (isActive()) startActivity()
    }

    override fun onAdInterFailShow() {
        if (isActive()) startActivity()
    }

    override fun onProgress(count: Int) {
        if (!isActive()) return
        if (jobSplash.isShowingAd()) return
        if (interManager.isShowingAd()) return
        viewBinding.progressBar.progress = count
        if (count >= 50 && !interManager.isLoadAd()) {
            jobSplash.setDelay(160)
        }
        if (interManager.isLoadAd() && !jobSplash.isShowingAd() && !interManager.isShowingAd()) {
            viewBinding.progressBar.hide()
            viewBinding.tvLoad.hide()
            jobSplash.setShowAds()
            jobSplash.stopJob()
            interManager.showAd(this)
        } else if ((!jobSplash.isShowingAd() && jobSplash.isProgressMax()) || interManager.isError()) {
            jobSplash.setShowAds()
            startActivity()
        }
    }

    private fun startActivity() {
        if (!isActive()) {
            return
        }
        jobSplash.stopJob()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}