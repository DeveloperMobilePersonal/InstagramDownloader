package instagram.photo.video.downloader.story.saver.ex

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import instagram.photo.video.downloader.story.saver.R
import java.io.File
import java.net.URLConnection

fun File?.share(context: Context) {
    if (this == null) {
        return
    }
    try {
        val intentBuilder = ShareCompat.IntentBuilder(context)
        intentBuilder
            .setStream(context.getUriFromFile(this))
            .setType(URLConnection.guessContentTypeFromName(this.name))
            .startChooser()
    } catch (e: Exception) {
        toastShow(R.string.txt_error)
    }
}

fun Context.getUriFromFile(file: File): Uri? =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
        Uri.fromFile(file)
    } else {
        try {
            FileProvider.getUriForFile(this, "$packageName.provider", file)
        } catch (e: Exception) {
            throw if (e.message?.contains("ProviderInfo.loadXmlMetaData") == true) {
                Error("FileProvider is not set or doesn't have needed permissions")
            } else {
                e
            }
        }
    }