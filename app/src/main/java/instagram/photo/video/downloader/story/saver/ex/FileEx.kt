package instagram.photo.video.downloader.story.saver.ex

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import java.io.File

private fun Context.getExternalPath(): String {
    val storageDirectoryPath: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val storageManager = getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val directory = storageManager.primaryStorageVolume.directory
            ?: return Environment.getExternalStorageDirectory().absolutePath
        directory.absolutePath
    } else {
        Environment.getExternalStorageDirectory().absolutePath
    }
    return storageDirectoryPath
}

fun Context.createAlbumStorage(): String {
    val path = getExternalPath() +
            File.separator +
            Environment.DIRECTORY_DCIM +
            File.separator +
            "In_Downloader"
    val file = File(path)
    if (!file.exists()) {
        file.mkdirs()
    }
    return path
}