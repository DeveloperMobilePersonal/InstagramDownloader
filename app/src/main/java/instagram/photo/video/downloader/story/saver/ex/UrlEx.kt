package instagram.photo.video.downloader.story.saver.ex

import instagram.photo.video.downloader.story.saver.model.UrlModel

fun String.getUrlRelease(): UrlModel? {
    return if (!hasUrlInstagramValid()) {
        null
    } else {
        if (hasUrlStoryInstagramValid()) {
            UrlModel(this, this, true)
        } else {
            val m = this.split("?igshid")
            if (m.isEmpty()) {
                val n = this.split("?")
                if (n.isEmpty()) {
                    null
                } else {
                    val url = "${n.first()}embed/captioned/?utm_source=ig_web_copy_link"
                    UrlModel(this, url, false)
                }
            } else {
                val url = "${m.first()}embed/captioned/?utm_source=ig_web_copy_link"
                UrlModel(this, url, false)
            }
        }
    }
}

fun String.hasUrlStoryInstagramValid(): Boolean {
    return hasUrlInstagramValid() && (this.contains("stories") || this.contains("story"))
}

fun String.hasUrlInstagramValid(): Boolean {
    return this.contains("https://www.instagram.com/".toRegex()) ||
            this.contains("https://instagram.com/".toRegex()) ||
            this.contains("https://m.instagram.com/".toRegex())
}