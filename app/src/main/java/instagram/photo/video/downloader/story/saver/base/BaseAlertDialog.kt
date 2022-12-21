package instagram.photo.video.downloader.story.saver.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import instagram.photo.video.downloader.story.saver.R

abstract class BaseAlertDialog<T : ViewDataBinding>(private val activity: BaseActivity<*>) :
    AlertDialog(activity) {

    lateinit var viewBinding: T
    private lateinit var alertDialog: AlertDialog

    init {
        setup()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), loadUI(), null, false)
        viewBinding.lifecycleOwner = activity
        setContentView(viewBinding.root)
        setCanceledOnTouchOutside(false)
        setCancelable(false)
        window?.setLayout(width(), height())
        if (animator() != -1) {
            window?.setWindowAnimations(animator())
        }
        if (transparent()) {
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        createUI()
        setOnCancelListener {
            destroyUI()
        }
        setOnDismissListener {
            destroyUI()
        }
    }

    private fun setup() {

    }

    protected abstract fun loadUI(): Int
    protected abstract fun createUI()
    protected abstract fun destroyUI()

    open fun showUI() {
        if (hasShowUI()) return
        show()
    }

    open fun hasShowUI(): Boolean {
        return isShowing
    }

    open fun hideUI() {
        if (!hasShowUI()) return
        dismiss()
    }

    open fun transparent(): Boolean {
        return false
    }

    open fun width(): Int {
        return ViewGroup.LayoutParams.MATCH_PARENT
    }

    open fun height(): Int {
        return ViewGroup.LayoutParams.WRAP_CONTENT
    }

    open fun animator(): Int {
        return R.style.AnimatorScale
    }
}