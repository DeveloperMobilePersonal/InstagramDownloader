package instagram.photo.video.downloader.story.saver.base

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class LogManager {

    companion object {
        @JvmStatic
        val TAG = "LogManager"

        @JvmStatic
        var isLogEnable = true

        @JvmStatic
        fun getCurrentMethod(): String {
            return try {
                val stacktraceObj = Thread.currentThread().stackTrace
                val stackTraceElement = stacktraceObj[5]
                var className = stackTraceElement.className
                className = className.substring(className.lastIndexOf(".") + 1, className.length)
                " [" + className + "] " + stackTraceElement.methodName
            } catch (e: Exception) {
                ""
            }
        }

        @JvmStatic
        fun composeDefaultMessage(message: String): String {
            return getCurrentMethod() + " = " + message
        }

        @JvmStatic
        fun state(enable: Boolean) {
            isLogEnable = enable
        }

        @JvmStatic
        fun showLog(message: Any?) {
            if (!isLogEnable) {
                return
            }
            val gson = GsonBuilder().disableHtmlEscaping().create()
            Log.i(TAG, composeDefaultMessage(gson.toJson(message ?: "null")))
        }
    }

}