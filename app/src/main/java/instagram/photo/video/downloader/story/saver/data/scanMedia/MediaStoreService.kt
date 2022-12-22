package instagram.photo.video.downloader.story.saver.data.scanMedia

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
import android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
import android.text.TextUtils
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.ex.createAlbumStorage
import java.util.concurrent.TimeUnit


class MediaStoreService(private val context: Context) {

    private val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Video.Media.DURATION
    )

    private val selection = (MediaStore.Files.FileColumns.DATA + " like ? ")

    private var selectionargs = arrayOf("%${context.createAlbumStorage()}%")

    @SuppressLint("Recycle")
    fun scan(): MutableList<GalleryModel> {
        val list = mutableListOf<GalleryModel>()
        val uri: Uri = MediaStore.Files.getContentUri("external")
        val cr: ContentResolver = context.contentResolver
        val cursor: Cursor? = cr.query(
            uri,
            projection,
            selection,
            selectionargs,
            MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
        )
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val id = getLong(cursor, getColumnIndex(cursor, projection[0]))
                val path = getString(cursor, getColumnIndex(cursor, projection[1]))
                val title = getString(cursor, getColumnIndex(cursor, projection[2]))
                val name = getString(cursor, getColumnIndex(cursor, projection[3]))
                val mediaType = getLong(cursor, getColumnIndex(cursor, projection[4]))
                if (mediaType.toInt() == MEDIA_TYPE_VIDEO || mediaType.toInt() == MEDIA_TYPE_IMAGE) {
                    val mDuration = if (mediaType.toInt() == MEDIA_TYPE_VIDEO) {
                        getLong(cursor, getColumnIndex(cursor, projection[5]))
                    } else -1
                    val duration = kotlin.runCatching {
                        if (mDuration <= 0) {
                            ""
                        } else {
                            val seconds = (TimeUnit.MILLISECONDS.toSeconds(mDuration) % 60).toInt()
                            val minutes = (TimeUnit.MILLISECONDS.toMinutes(mDuration)).toInt()
                            String.format(" %02d:%02d", minutes, seconds)
                        }
                    }.getOrElse { "" }
                    list.add(GalleryModel(id, path, title, name, duration))
                }
            }
        }
        cursor?.close()
        return list
    }

    private fun getColumnIndex(cursor: Cursor, params: String): Int {
        return cursor.getColumnIndex(params)
    }

    private fun getLong(cursor: Cursor, index: Int): Long {
        return try {
            cursor.getLong(index)
        } catch (e: Exception) {
            -1
        }
    }

    private fun getString(cursor: Cursor, index: Int): String {
        return try {
            val txt = cursor.getString(index)
            if (TextUtils.isEmpty(txt) || TextUtils.equals(txt, MediaStore.UNKNOWN_STRING)) {
                MediaStore.UNKNOWN_STRING
            } else {
                txt
            }
        } catch (e: Exception) {
            MediaStore.UNKNOWN_STRING
        }
    }
}