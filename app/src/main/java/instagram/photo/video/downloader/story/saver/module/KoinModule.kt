package instagram.photo.video.downloader.story.saver.module

import instagram.photo.video.downloader.story.saver.ads.appopen.AdAppOpenApplication
import instagram.photo.video.downloader.story.saver.ads.inter.InterManager
import instagram.photo.video.downloader.story.saver.data.ClipManager
import instagram.photo.video.downloader.story.saver.data.DataService
import instagram.photo.video.downloader.story.saver.data.DownloaderService
import instagram.photo.video.downloader.story.saver.data.scanMedia.MediaStoreService
import instagram.photo.video.downloader.story.saver.dialog.*
import instagram.photo.video.downloader.story.saver.permission.PermissionManager
import instagram.photo.video.downloader.story.saver.ui.MainActivity
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryActivity
import instagram.photo.video.downloader.story.saver.ui.gallery.GalleryViewModel
import instagram.photo.video.downloader.story.saver.ui.gallery.adapter.GalleryAdapter
import instagram.photo.video.downloader.story.saver.ui.splash.JobSplash
import instagram.photo.video.downloader.story.saver.ui.splash.SplashActivity
import instagram.photo.video.downloader.story.saver.unit.ID_APPLICATION_APP_OPEN
import instagram.photo.video.downloader.story.saver.unit.ID_MAIN_INTER
import instagram.photo.video.downloader.story.saver.unit.ID_SPLASH_INTER
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
        factory { DialogPermissionError(get()) }
        factory { DialogRate(get()) }
        factory { InterManager(get(), ID_MAIN_INTER) }
    }
    scope<GalleryActivity> {
        viewModel{GalleryViewModel(MediaStoreService(get()))}
        factory { GalleryAdapter(get()) }
    }

    single { AdAppOpenApplication(get(), ID_APPLICATION_APP_OPEN) }

    scope<SplashActivity> {
        factory { InterManager(get(), ID_SPLASH_INTER) }
        factory { JobSplash() }
    }
}
val listModule = listOf(activityModule)
