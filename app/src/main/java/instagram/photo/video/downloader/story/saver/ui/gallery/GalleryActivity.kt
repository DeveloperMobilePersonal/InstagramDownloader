package instagram.photo.video.downloader.story.saver.ui.gallery

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplication
import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplicationListener
import instagram.photo.video.downloader.story.saver.ads.appopen.AppOpenManager
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.databinding.ActivityGalleryBinding
import instagram.photo.video.downloader.story.saver.ex.*
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.GalleryAdapter
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.OnItemGalleryListener
import instagram.photo.video.downloader.story.saver.ui.preview.PreviewActivity
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity<ActivityGalleryBinding>(),
    OnItemGalleryListener,
    AdAppOpenApplicationListener {

    private val viewModel by viewModel<GalleryViewModel>()
    private val galleryAdapter: GalleryAdapter by inject()
    private val adAppOpenApplication by inject<AdAppOpenApplication>()

    override fun loadUI(): Int {
        return R.layout.activity_gallery
    }

    override fun createUI() {
        viewBinding.viewModel = viewModel
        viewBinding.recyclerview.disableAnimator()
        viewBinding.recyclerview.spacing(com.intuit.sdp.R.dimen._1sdp)
        viewBinding.recyclerview.gridLayoutManager(galleryAdapter, 3)
        viewBinding.recyclerview.adapter = galleryAdapter
        galleryAdapter.listener = this
        viewBinding.btBack.click {
            finish()
        }
        viewBinding.imgNoData.load(R.drawable.ic_no_data, R.drawable.shape_loading_glide)
        viewModel.fetch()
    }

    override fun onResume() {
        adAppOpenApplication.addListener(this)
        super.onResume()
    }

    override fun destroyUI() {

    }

    override fun onItemGalleryClick(galleryModel: GalleryModel, position: Int) {
        if (!isActive()) return
        lifecycleScope.launch {
            putShared(KEY_PREVIEW_CACHE, Gson().toJson(galleryModel))
            val intent = Intent(this@GalleryActivity, PreviewActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onAdStartAppOpen(appOpenManager: AppOpenManager) {
        if (isActive()) appOpenManager.showAd(this)
    }

}