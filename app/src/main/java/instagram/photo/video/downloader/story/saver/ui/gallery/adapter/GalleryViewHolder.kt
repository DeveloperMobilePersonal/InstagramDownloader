package instagram.photo.video.downloader.story.saver.ui.gallery.adapter

import android.content.Context
import instagram.photo.video.downloader.story.saver.base.BaseListViewHolder
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.databinding.ItemGalleryBinding

class GalleryViewHolder(context: Context, viewBinding: ItemGalleryBinding) :
    BaseListViewHolder<GalleryModel, ItemGalleryBinding>(context, viewBinding) {

    fun bind(model: GalleryModel) {
        viewBinding.model = model
    }
}