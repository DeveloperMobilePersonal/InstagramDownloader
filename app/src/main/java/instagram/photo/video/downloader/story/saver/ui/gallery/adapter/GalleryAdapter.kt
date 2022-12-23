package instagram.photo.video.downloader.story.saver.ui.gallery.adapter

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.databinding.ItemGalleryBinding
import instagram.photo.video.downloader.story.saver.ex.clear
import instagram.photo.video.downloader.story.saver.ex.click

class GalleryAdapter(private val context: Context) :
    ListAdapter<GalleryModel, GalleryViewHolder>(AsyncDifferConfig.Builder(Diff()).build()) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    var listener: OnItemGalleryListener? = null
    private var animateScale = false

    class Diff : DiffUtil.ItemCallback<GalleryModel>() {
        override fun areItemsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: GalleryModel, newItem: GalleryModel): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    override fun submitList(list: MutableList<GalleryModel>?) {
        animateScale = true
        val l = mutableListOf<GalleryModel>()
        l.addAll(list ?: mutableListOf())
        super.submitList(l) {
            Handler(Looper.getMainLooper()).postDelayed({ animateScale = false }, 100)
        }
    }

    override fun submitList(list: MutableList<GalleryModel>?, commitCallback: Runnable?) {
        val l = mutableListOf<GalleryModel>()
        l.addAll(list ?: mutableListOf())
        super.submitList(l, commitCallback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(context, ItemGalleryBinding.inflate(layoutInflater, parent, false))
    }

    fun addListener(listener: OnItemGalleryListener) {
        this.listener = listener
    }

    fun reset() {
        submitList(null)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val galleryModel = currentList[position]
        holder.bind(galleryModel)
        holder.viewBinding.root.click {
            val modelCurrent = currentList[holder.absoluteAdapterPosition]
            listener?.onItemGalleryClick(modelCurrent, position)
        }
    }

    override fun onViewRecycled(holder: GalleryViewHolder) {
        holder.viewBinding.imgPreview.clear()
        super.onViewRecycled(holder)
    }


}