package instagram.photo.video.downloader.story.saver.ui.gallery

import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.databinding.ActivityGalleryBinding
import instagram.photo.video.downloader.story.saver.ex.click
import instagram.photo.video.downloader.story.saver.ex.disableAnimator
import instagram.photo.video.downloader.story.saver.ex.gridLayoutManager
import instagram.photo.video.downloader.story.saver.ex.spacing
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.GalleryAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class GalleryActivity : BaseActivity<ActivityGalleryBinding>() {

    private val viewModel by viewModel<GalleryViewModel>()
    private val galleryAdapter: GalleryAdapter by inject()

    override fun loadUI(): Int {
        return R.layout.activity_gallery
    }

    override fun createUI() {
        viewBinding.viewModel = viewModel
        viewBinding.recyclerview.disableAnimator()
        viewBinding.recyclerview.spacing(com.intuit.sdp.R.dimen._1sdp)
        viewBinding.recyclerview.gridLayoutManager(galleryAdapter, 3)
        viewBinding.recyclerview.adapter = galleryAdapter
        viewBinding.btBack.click {
            finish()
        }
        viewModel.fetch()
    }

    override fun destroyUI() {

    }

}