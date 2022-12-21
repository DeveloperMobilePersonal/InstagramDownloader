package instagram.photo.video.downloader.story.saver.app

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
object ContextGlobal {

    private var context: Context? = null

    fun set(context: Context) {
        ContextGlobal.context = context
    }

    fun get(): Context {
        return context!!
    }
}