package instagram.photo.video.downloader.story.saver.data

import android.content.Context
import android.provider.MediaStore
import androidx.lifecycle.LifecycleCoroutineScope
import com.androidnetworking.AndroidNetworking
import com.google.gson.GsonBuilder
import instagram.photo.video.downloader.story.saver.app.ContextGlobal
import instagram.photo.video.downloader.story.saver.base.LogManager
import instagram.photo.video.downloader.story.saver.data.instagram.DataModel
import instagram.photo.video.downloader.story.saver.ex.KEY_URL_CACHE
import instagram.photo.video.downloader.story.saver.ex.getSharedString
import instagram.photo.video.downloader.story.saver.model.UrlModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.commons.text.StringEscapeUtils
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.math.abs

class DataService(
    private val context: Context,
    private val coroutineScope: LifecycleCoroutineScope,
    private val listener: DataListener? = null
) {

    private var urlCache = ""
    var mediaSource: MediaSource? = null

    fun isFocusChanged(url: String): Boolean {
        return url.isNotEmpty() && urlCache != url && ContextGlobal.get()
            .getSharedString(KEY_URL_CACHE, "") != url
    }

    fun fetch(urlModel: UrlModel) {
        urlCache = urlModel.url
        coroutineScope.launch(Dispatchers.IO) {
            val mediaSource = kotlin.runCatching { fetchJsoup(urlModel.urlNew) }.getOrElse { null }
            if (mediaSource == null) {
                val mMediaSource = kotlin.runCatching { fetchAndroidNetworking(urlModel.urlNew) }
                    .getOrElse {
                        LogManager.showLog(it)
                        null
                    }
                if (mMediaSource == null) {
                    withContext(Dispatchers.Main) {
                        listener?.onDataError()
                    }
                } else {
                    urlCache = urlModel.url
                    this@DataService.mediaSource = mMediaSource
                    withContext(Dispatchers.Main) {
                        listener?.onDataMediaSource(mMediaSource)
                    }
                }
            } else {
                urlCache = urlModel.url
                this@DataService.mediaSource = mediaSource
                withContext(Dispatchers.Main) {
                    listener?.onDataMediaSource(mediaSource)
                }
            }
        }
    }

    fun fetchStory(urlModel: UrlModel) {
        urlCache = urlModel.url
        coroutineScope.launch(Dispatchers.IO) {
            val mediaSource = kotlin.runCatching { fetchStoryRapAPI(urlModel) }.getOrElse { null }
            if (mediaSource == null) {
                withContext(Dispatchers.Main) {
                    listener?.onDataError()
                }
            } else {
                urlCache = urlModel.url
                this@DataService.mediaSource = mediaSource
                withContext(Dispatchers.Main) {
                    listener?.onDataMediaSource(mediaSource)
                }
            }
        }
    }

    private fun fetchJsoup(url: String): MediaSource? {
        val document = Jsoup.connect(url)
            .ignoreContentType(true)
            .userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36")
            .referrer("http://www.google.com")
            .timeout(10000)
            .followRedirects(true)
            .maxBodySize(0)
            .get()
        val scripts = document.select("script")
        if (!scripts.isNullOrEmpty()) {
            scripts.forEach {
                val html = it.html()
                if (html.contains("window.__additionalDataLoaded\\('extra',".toRegex())) {
                    val json = html.plus("replace_data")
                        .replace("window.__additionalDataLoaded\\('extra',".toRegex(), "")
                        .replace(");replace_data", "")
                    val gson = GsonBuilder().create()
                    val dataModel = gson.fromJson(
                        StringEscapeUtils.unescapeJava(json),
                        DataModel::class.java
                    )
                    return dataModel.exportMediaSource()!!
                }
            }
        }
        return null
    }

    private fun fetchAndroidNetworking(url: String): MediaSource? {
        val newUrl = url.replace("embed/captioned/?utm_source=ig_web_copy_link", "")
        val build = AndroidNetworking.get(newUrl)
            .addHeaders("Accept", "application/json")
            .addHeaders("Content-Type", "application/json;charset=UTF-8")
            .addHeaders(
                "user-agent",
                "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.102 Safari/537.36"
            )
            .addHeaders("x-requested-with", "XMLHttpRequest")
            .addQueryParameter("__a", "1")
            .addQueryParameter("__d", "dis")
            .build()
        val execute = build.executeForJSONObject()
        return if (execute.isSuccess) {
            val result = execute.result as JSONObject
            val objectGraphql = result.getString("graphql")
            val gson = GsonBuilder().create()
            val dataModel = gson.fromJson(
                objectGraphql,
                DataModel::class.java
            )
            dataModel.exportMediaSource()!!
        } else null
    }

    private fun fetchStoryRapAPI(urlModel: UrlModel): MediaSource? {
        val newUrl = urlModel.url.split("?").first()
        val id = kotlin.runCatching {
            newUrl.split("/").last()
        }.getOrElse {
            abs(urlModel.url.hashCode()).toString()
        }
        val build =
            AndroidNetworking.get("https://instagram-media-downloader.p.rapidapi.com/rapid/stories.php?url=$newUrl")
                .addHeaders("X-RapidAPI-Key", "6b16fe074cmshc45cb729c345e57p115139jsne0ce3e03e777")
                .addHeaders("X-RapidAPI-Host", "instagram-media-downloader.p.rapidapi.com")
                .build()
        val execute = build.executeForJSONObject()
        return if (execute.isSuccess) {
            val result = execute.result as JSONObject
            val video = kotlin.runCatching { result.getString("video") }.getOrNull()
            val image = kotlin.runCatching { result.getString("image") }.getOrNull()
            val mediaSource = MediaSource()
            val list = mutableListOf<MediaSource.ResourceModel>()
            if (!image.isNullOrEmpty()) {
                list.add(
                    MediaSource.ResourceModel(
                        id, image,
                        MediaSource.ResourceModel.Companion.MediaType.PHOTO
                    )
                )
            }
            if (!video.isNullOrEmpty()) {
                list.add(
                    MediaSource.ResourceModel(
                        id, video,
                        MediaSource.ResourceModel.Companion.MediaType.VIDEO
                    )
                )
            }
            mediaSource.resources = list
            mediaSource.id = id
            mediaSource.username = MediaStore.UNKNOWN_STRING
            mediaSource.caption = MediaStore.UNKNOWN_STRING
            mediaSource.profilePicUrl = MediaStore.UNKNOWN_STRING
            mediaSource.fullName = MediaStore.UNKNOWN_STRING
            mediaSource
        } else {
            null
        }
    }

    private fun writeToFile(data: String) {
        try {
            val file = File(context.getExternalFilesDir(null), "html.json")
            FileOutputStream(file).use {
                it.write(data.toByteArray())
            }
            LogManager.showLog(file.absolutePath)
        } catch (e: IOException) {
            LogManager.showLog(e.toString())
        }
    }
}