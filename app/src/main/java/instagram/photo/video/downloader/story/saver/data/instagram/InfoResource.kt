package instagram.photo.video.downloader.story.saver.data.instagram

import com.google.gson.annotations.SerializedName

class InfoResource(

    @SerializedName("src")
    val src: String = "",

    @SerializedName("config_width")
    val width: Int = 0,

    @SerializedName("config_height")
    val height: Int = 0

)