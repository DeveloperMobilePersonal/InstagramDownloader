package instagram.photo.video.downloader.story.saver.ex

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.gson.Gson
import instagram.photo.video.downloader.story.saver.data.MediaSource

const val KEY_URL_CACHE = "key_url_cache"
const val KEY_MEDIA_SOURCE_CACHE = "key_media_source_cache"

val Context.getSharedPreferences: SharedPreferences
    get() = getSharedPreferences(
        "in_downloader",
        MODE_PRIVATE
    )

@SuppressLint("CommitPrefEdits")
fun Context.putShared(key: String, any: Any) {
    val edit = getSharedPreferences.edit()
    when (any) {
        is String -> {
            edit.putString(key, any)
        }
        is Float -> {
            edit.putFloat(key, any)
        }
        is Boolean -> {
            edit.putBoolean(key, any)
        }
        is Int -> {
            edit.putInt(key, any)
        }
        is Long -> {
            edit.putLong(key, any)
        }
    }

    edit.apply()
}

fun Context?.getSharedString(key: String, default: String = ""): String {
    if (this == null) {
        return ""
    }
    return getSharedPreferences.getString(key, default) ?: default
}

fun Context?.getSharedInt(key: String, default: Int = -1): Int {
    if (this == null) {
        return -1
    }
    return getSharedPreferences.getInt(key, default)
}

fun Context?.getSharedLong(key: String, default: Long = 0): Long {
    if (this == null) {
        return -1
    }
    return getSharedPreferences.getLong(key, default)
}

fun Context?.getSharedFloat(key: String, default: Float = 0f): Float {
    if (this == null) {
        return default
    }
    return getSharedPreferences.getFloat(key, default)
}

fun Context?.getSharedBoolean(key: String, default: Boolean = false): Boolean {
    if (this == null) {
        return true
    }
    return getSharedPreferences.getBoolean(key, default)
}

fun Context.getMediaSourceCache(): MediaSource? {
    val sharedString = getSharedString(KEY_MEDIA_SOURCE_CACHE)
    if (sharedString.isEmpty()) return null
    return kotlin.runCatching {
        Gson().fromJson(sharedString, MediaSource::class.java)
    }.getOrNull()
}