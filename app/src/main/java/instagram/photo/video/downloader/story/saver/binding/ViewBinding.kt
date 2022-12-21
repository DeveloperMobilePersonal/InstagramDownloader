package instagram.photo.video.downloader.story.saver.binding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.ex.gone
import instagram.photo.video.downloader.story.saver.ex.load
import instagram.photo.video.downloader.story.saver.ex.show
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.GalleryAdapter

object ViewBinding {
    @JvmStatic
    @BindingAdapter("gallery")
    fun gallery(recyclerView: RecyclerView, list: MutableList<GalleryModel>?) {
        if (list.isNullOrEmpty()) {
            recyclerView.gone()
            return
        }
        recyclerView.show()
        val adapter = recyclerView.adapter
        if (adapter is GalleryAdapter) {
            adapter.submitList(list)
        }
    }

    @JvmStatic
    @BindingAdapter("loadThumb")
    fun loadThumb(view: ImageView, galleryModel: GalleryModel?) {
        if (galleryModel == null) {
            return
        }
        view.load(galleryModel.path, R.drawable.shape_loading_glide)
    }

    @JvmStatic
    @BindingAdapter("loadImage")
    fun loadImage(view: ImageView, any: Any?) {
        if (any == null) {
            return
        }
        view.load(any, R.drawable.shape_loading_glide)
    }

    @JvmStatic
    @BindingAdapter("loadTimeVideo")
    fun loadTimeVideo(view: View, time: String?) {
        if (time.isNullOrEmpty()) {
            view.gone()
            return
        }
        view.show()
        if (view is AppCompatTextView) {
            view.text = time
        }
    }

    @JvmStatic
    @BindingAdapter("loadSwipeRefresh")
    fun loadSwipeRefresh(view: SwipeRefreshLayout, state: Boolean?) {
        if (state == null) {
            return
        }
        view.isRefreshing = state
    }
}