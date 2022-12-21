package instagram.photo.video.downloader.story.saver.ex

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import instagram.photo.video.downloader.story.saver.app.ContextGlobal

fun Context.toastShow(@StringRes messageId: Int, text: String = "") {
    try {
        Toast.makeText(this, getString(messageId, text), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
    }
}

fun Context.toastShow(text: String = "") {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}

fun toastShow(@StringRes messageId: Int, text: String = "") {
    val context = ContextGlobal.get()
    try {
        Toast.makeText(context, context.getString(messageId, text), Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
    }
}

fun Context.getColorCompat(@ColorRes id: Int): Int {
    return ResourcesCompat.getColor(resources, id, null)
}

fun Context.getStringCompat(@StringRes id: Int): String {
    return getString(id)
}

fun getStringCompat(@StringRes id: Int): String {
    return ContextGlobal.get().getString(id)
}

fun String.stringBuilderBold(
    text: String = "",
    color: Int = Color.BLACK,
    style: Int = Typeface.BOLD
): SpannableStringBuilder {
    val messageBuilder = SpannableStringBuilder(this)
    val fcs = ForegroundColorSpan(color)
    val b = StyleSpan(style)
    messageBuilder.setSpan(
        b,
        indexOf(text),
        indexOf(text) + text.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    messageBuilder.setSpan(
        fcs,
        indexOf(text),
        indexOf(text) + text.length,
        Spannable.SPAN_INCLUSIVE_INCLUSIVE
    )
    return messageBuilder
}