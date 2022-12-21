package instagram.photo.video.downloader.story.saver.ui.gallery.adapter

import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel

interface OnItemGalleryListener {
    fun onItemGalleryClick(galleryModel: GalleryModel, position: Int)
}