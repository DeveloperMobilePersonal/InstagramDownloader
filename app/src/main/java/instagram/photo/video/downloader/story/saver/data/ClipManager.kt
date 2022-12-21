package instagram.photo.video.downloader.story.saver.data

import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipDescription.MIMETYPE_TEXT_HTML
import android.content.ClipDescription.MIMETYPE_TEXT_PLAIN
import android.content.ClipboardManager
import android.content.Context
import android.util.Log

class ClipManager(private val context: Context) {

    private var clipboard: ClipboardManager =
        context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    fun resetClipboard() {
        try {
            val clip = ClipData.newPlainText("", null)
            clipboard.setPrimaryClip(clip)
        } catch (ignored: Exception) {

        }
    }

    fun getTextCopy(): String {
        var pasteData = ""
        val primaryClipDescription = clipboard.primaryClipDescription ?: return pasteData
        if (clipboard.hasPrimaryClip() && primaryClipDescription.hasMimeType(MIMETYPE_TEXT_PLAIN)) {
            val primaryClip = clipboard.primaryClip ?: return pasteData
            if (primaryClip.itemCount <= 0) {
                return pasteData
            }
            pasteData = try {
                primaryClip.getItemAt(0).coerceToText(context).toString()
            } catch (e: Exception) {
                primaryClip.getItemAt(0).text.toString()
            }
        }
        return pasteData
    }

    private fun ClipboardManager.isPrimaryClipPlainText() = primaryClipDescription?.hasMimeType(
        MIMETYPE_TEXT_PLAIN
    ) ?: false

    private fun ClipboardManager.isPrimaryClipHtmlText() =
        primaryClipDescription?.hasMimeType(MIMETYPE_TEXT_HTML) ?: false

}