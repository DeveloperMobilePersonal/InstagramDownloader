package instagram.photo.video.downloader.story.saver.ui.preview

import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.gson.Gson
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.base.BaseActivity
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.databinding.ActivityPreviewBinding
import instagram.photo.video.downloader.story.saver.ex.*
import kotlinx.coroutines.launch
import java.io.File

class PreviewActivity : BaseActivity<ActivityPreviewBinding>() {

    private val exoPlayer by lazy {
        ExoPlayer.Builder(this).build()
    }

    override fun loadUI(): Int {
        return R.layout.activity_preview
    }

    override fun createUI() {
        lifecycleScope.launch {
            val galleryModel = kotlin.runCatching {
                val data = getSharedString(KEY_PREVIEW_CACHE, "")
                Gson().fromJson(data, GalleryModel::class.java)
            }.getOrNull()
            if (galleryModel == null || !File(galleryModel.path).exists()) {
                finish()
            } else {
                val file = File(galleryModel.path)
                if (galleryModel.type == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO) {
                    exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
                    viewBinding.videoView.show()
                    viewBinding.videoView.player = exoPlayer
                    val mediaSource = kotlin.runCatching {
                        DataSpec(file.toUri())
                            .let { FileDataSource().apply { open(it) } }
                            .let { DataSource.Factory { it } }
                            .let { ProgressiveMediaSource.Factory(it, DefaultExtractorsFactory()) }
                            .createMediaSource(MediaItem.fromUri(file.toUri()))
                    }.getOrNull()
                    if (mediaSource == null) {
                        finish()
                        return@launch
                    }
                    exoPlayer.setMediaSource(mediaSource)
                    exoPlayer.prepare()
                    exoPlayer.playWhenReady = true
                } else {
                    viewBinding.zoomImageView.show()
                    viewBinding.zoomImageView.load(
                        galleryModel.path,
                        R.drawable.shape_loading_glide
                    )
                }
                viewBinding.btShare.setOnClickListener {
                    file.share(this@PreviewActivity)
                }
            }
        }
        viewBinding.btBack.click {
            onBackPressed()
        }
    }

    override fun onPause() {
        exoPlayer.playWhenReady = false
        super.onPause()
    }

    override fun onResume() {
        exoPlayer.playWhenReady = true
        super.onResume()
    }

    override fun onBackPressed() {
        exoPlayer.clearMediaItems()
        exoPlayer.release()
        super.onBackPressed()
    }

    override fun destroyUI() {
        exoPlayer.clearMediaItems()
        exoPlayer.release()
    }
}