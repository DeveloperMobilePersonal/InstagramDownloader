package instagram.photo.video.downloader.story.saver.ex

import android.annotation.SuppressLint
import java.text.CharacterIterator
import java.text.SimpleDateFormat
import java.text.StringCharacterIterator
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("DefaultLocale")
fun Long.byteFormat(): String {
    var bytes = this
    if (-1000 < bytes && bytes < 1000) {
        return "$bytes B"
    }
    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
    while (bytes <= -999950 || bytes >= 999950) {
        bytes /= 1000
        ci.next()
    }
    return java.lang.String.format("%.1f %cB", bytes / 1000.0, ci.current())
}

@SuppressLint("SimpleDateFormat")
fun Long.dateFormat(): String {
    val date = Date(this)
    return SimpleDateFormat("EEEE, dd-MM-yyyy HH:mm").format(date)
}

fun Float.max(max: Float): Float {
    if (this > max) {
        return max
    }
    return this
}

fun Long.time(): String {
    return try {
        val time = this
        val seconds = (TimeUnit.MILLISECONDS.toSeconds(time) % 60).toInt()
        val minutes = (TimeUnit.MILLISECONDS.toMinutes(time)).toInt()
        String.format("%02d:%02d", minutes, seconds)
    } catch (e: Exception) {
        "00:00"
    }
}
