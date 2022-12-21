package instagram.photo.video.downloader.story.saver.data

import android.content.Context
import android.media.MediaScannerConnection
import android.os.Build
import androidx.lifecycle.LifecycleCoroutineScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import instagram.photo.video.downloader.story.saver.BuildConfig
import instagram.photo.video.downloader.story.saver.app.ContextGlobal
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.ex.createAlbumStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.util.*

class DownloaderService(
    private val context: Context,
    private val listener: DownloaderListener? = null,
    private val coroutineScope: LifecycleCoroutineScope
) {

    private var job: Job? = null
    private var listError = mutableListOf<String>()

    fun stop() {
        job?.cancel()
    }

    fun start(resources: MutableList<MediaSource.ResourceModel>) {
        listError.clear()
        if (resources.isEmpty()) {
            listener?.onDownloadError()
            return
        }
        listener?.onDownloadStart()
        job = coroutineScope.launch { downloader(0, resources) }
    }

    private fun downloader(
        count: Int,
        resources: MutableList<MediaSource.ResourceModel>
    ) {
        val resource = resources[count]
        listener?.onDownloadProgress(count, resources.size)
        val pathStorage = context.createAlbumStorage()
        val extension =
            if (resource.mediaType == MediaSource.ResourceModel.Companion.MediaType.PHOTO) {
                "_photo.jpg"
            } else "_video.mp4"
        val path = pathStorage + "/${resource.id}$extension"
        val file = File(path)
        if (file.exists()) {
            if (count < resources.size - 1) {
                downloader(count + 1, resources)
            } else {
                listener?.onDownloadComplete()
            }
        } else {
            AndroidNetworking.download(resource.url, pathStorage, "${resource.id}$extension")
                .setTag(UUID.randomUUID().toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        MediaScannerConnection.scanFile(
                            ContextGlobal.get(),
                            arrayOf(path),
                            null,
                            null
                        )
                        if (count < resources.size - 1) {
                            downloader(count + 1, resources)
                        } else {
                            listener?.onDownloadComplete()
                        }
                    }

                    override fun onError(anError: ANError?) {
                        listError.add("")
                        if (count < resources.size - 1) {
                            downloader(count + 1, resources)
                        } else {
                            if (listError.size == resources.size) {
                                listener?.onDownloadError()
                            } else listener?.onDownloadComplete()
                        }
                    }
                })
        }
    }
}