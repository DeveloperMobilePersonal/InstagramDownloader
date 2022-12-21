package instagram.photo.video.downloader.story.saver.ex

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.ProgressBar
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import instagram.photo.video.downloader.story.saver.R
import instagram.photo.video.downloader.story.saver.app.ContextGlobal

fun ImageView.clear() {
    kotlin.runCatching {
        Glide.with(ContextGlobal.get()).clear(this)
        setImageDrawable(null)
    }
}

fun ImageView.load(path: Any, placeholder: Int = R.drawable.shape_loading_glide) {
    val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
    Glide.with(ContextGlobal.get())
        .load(path)
        .placeholder(placeholder)
        .apply(requestOptions)
        .dontAnimate()
        .into(this)
}

fun ImageView.load(path: Any, progressBar: ProgressBar? = null) {
    kotlin.runCatching { progressBar?.show() }
    Glide.with(ContextGlobal.get())
        .load(path)
        .listener {
            kotlin.runCatching { progressBar?.gone() }
        }
        .into(this)
}

@SuppressLint("CheckResult")
fun <T> RequestBuilder<T>.listener(result: ((T?) -> Unit)? = null): RequestBuilder<T> {
    addListener(object : RequestListener<T> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<T>?,
            isFirstResource: Boolean
        ): Boolean {
            result?.let { it(null) }
            return false
        }

        override fun onResourceReady(
            resource: T,
            model: Any?,
            target: Target<T>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            result?.let { it(resource) }
            return false
        }
    })
    return this
}
