package instagram.photo.video.downloader.story.saver.base

import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.gms.ads.AdSize
import org.koin.android.scope.AndroidScopeComponent
import org.koin.core.scope.Scope
import video.photo.instagram.downloader.story.module.contextAwareActivityScope

/**
 *  buildFeatures {
dataBinding true
}
 * */
abstract class BaseActivity<T : ViewDataBinding> : AppCompatActivity(), AndroidScopeComponent {

    override val scope: Scope by contextAwareActivityScope()

    private var foreground = false
    lateinit var viewBinding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isActive()) {
            loadAd()
            viewBinding = DataBindingUtil.setContentView(this, loadUI())
            viewBinding.lifecycleOwner = this
            createUI()
        }
    }

    override fun onResume() {
        foreground = true
        super.onResume()
    }

    override fun onPause() {
        foreground = false
        super.onPause()
    }

    override fun onDestroy() {
        destroyUI()
        super.onDestroy()
    }

    protected abstract fun loadUI(): Int
    protected abstract fun createUI()
    protected abstract fun destroyUI()

    /*
    * true : Activity run
    * false : Activity destroy
    * */
    fun isActive(): Boolean {
        return !isFinishing && !isDestroyed
    }

    /*
    * true : Activity Foreground
    * false : Activity Background
    * */
    fun isForeground(): Boolean {
        return foreground && isActive()
    }

    open fun loadAd() {

    }

    fun adSize(frameBannerAds: FrameLayout): AdSize {
        val outMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }

        val density = outMetrics.density

        var adWidthPixels = frameBannerAds.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }

        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
    }

}