package instagram.photo.video.downloader.story.saver.module

import instagram.photo.video.downloader.story.saver.data.ClipManager
import instagram.photo.video.downloader.story.saver.data.DataService
import instagram.photo.video.downloader.story.saver.data.DownloaderService
import instagram.photo.video.downloader.story.saver.data.scanMedia.MediaStoreService
import instagram.photo.video.downloader.story.saver.dialog.DialogDownloader
import instagram.photo.video.downloader.story.saver.dialog.DialogError
import instagram.photo.video.downloader.story.saver.dialog.DialogLoading
import instagram.photo.video.downloader.story.saver.permission.PermissionManager
import instagram.photo.video.downloader.story.saver.ui.MainActivity
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryActivity
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryViewModel
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.GalleryAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val activityModule = module {
    scope<MainActivity> {
        factory { ClipManager(get()) }
        factory { PermissionManager(get()) }
        factory { DialogLoading(get()) }
        factory { DialogError(get()) }
        factory { DataService(get(), get(), get()) }
        factory { DownloaderService(get(), get(), get()) }
        factory { DialogDownloader(get(), get()) }
    }
    scope<GalleryActivity> {
        viewModel{GalleryViewModel(MediaStoreService(get()))}
        factory { GalleryAdapter(get()) }
    }
}
val listModule = listOf(activityModule)
