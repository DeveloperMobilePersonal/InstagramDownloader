package instagram.photo.video.downloader.story.saver.ex

import android.view.View
import android.view.ViewGroup
import androidx.transition.TransitionManager

fun View.click(result: ((View) -> Unit)) {
    setOnClickListener {
        isEnabled = false
        result(this)
        kotlin.runCatching {
            postDelayed({ isEnabled = true }, 200)
        }
    }
}

fun View.disableDoubleClick() {
    isEnabled = false
    kotlin.runCatching {
        postDelayed({ isEnabled = true }, 200)
    }
}


fun View.show() {
    alpha = 1f
    if (visibility == View.VISIBLE) {
        return
    }
    visibility = View.VISIBLE
}

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.hide() {
    if (visibility == View.INVISIBLE) {
        return
    }
    visibility = View.INVISIBLE
}

fun View.gone() {
    if (visibility == View.GONE) {
        return
    }
    visibility = View.GONE
}

fun View.height(sizeH: Int) {
    var layoutParams = this.layoutParams
    if (layoutParams == null) {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    layoutParams.height = sizeH
    this.layoutParams = layoutParams
}

fun View.width(sizeW: Int) {
    var layoutParams = this.layoutParams
    if (layoutParams == null) {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    layoutParams.width = sizeW
    this.layoutParams = layoutParams
}

fun animate(viewGroup: ViewGroup) {
    TransitionManager.beginDelayedTransition(viewGroup)
}


