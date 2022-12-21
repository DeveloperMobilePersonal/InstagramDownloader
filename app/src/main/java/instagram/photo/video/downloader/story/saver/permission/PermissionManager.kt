package instagram.photo.video.downloader.story.saver.permission

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat

const val RQ_CODE_READ_EXTERNAL_STORAGE = 123
const val RQ_CODE_RESUME_READ_EXTERNAL_STORAGE = 1234
const val EXTRA_PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
const val EXTRA_PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE

class PermissionManager(private val activity: Activity) {

    interface PermissionListener {
        fun onPermissionAllow()
        fun onPermissionDenied()
        fun onPermissionAskAgain()
    }

    fun remove() {
        listener = null
        handler.removeCallbacksAndMessages(null)
    }

    private var listener: PermissionListener? = null
    private var rQCodeSetting: Int = -1
    private var rQCodeChange: Int = -1
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    fun requestPermissionsReadWriteStorage(listener: PermissionListener?) {
        if (isPermissionReadWriteStorage()) {
            listener?.onPermissionAllow()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestPermissions(
                RQ_CODE_READ_EXTERNAL_STORAGE,
                listener,
                EXTRA_PERMISSION_READ_EXTERNAL_STORAGE
            )
        } else {
            requestPermissions(
                RQ_CODE_READ_EXTERNAL_STORAGE,
                listener,
                EXTRA_PERMISSION_READ_EXTERNAL_STORAGE,
                EXTRA_PERMISSION_WRITE_EXTERNAL_STORAGE
            )
        }
    }

    fun isPermissionReadWriteStorage(): Boolean {
        return isPermission(EXTRA_PERMISSION_READ_EXTERNAL_STORAGE) &&
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    isPermission(EXTRA_PERMISSION_WRITE_EXTERNAL_STORAGE)
                } else {
                    true
                }
    }

    private fun isPermissionRationaleReadWriteStorage(activity: Activity): Boolean {
        return isPermissionRationale(activity, EXTRA_PERMISSION_READ_EXTERNAL_STORAGE) ||
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    isPermissionRationale(activity, EXTRA_PERMISSION_WRITE_EXTERNAL_STORAGE)
                } else {
                    false
                }
    }


    private val runnable = Runnable {
        if (isPermissionReadWriteStorage()) {
            listener?.onPermissionAllow()
        } else {
            postHandler()
        }
    }

    private fun postHandler() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(runnable, 100)
    }

    private fun isPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun isPermissionRationale(activity: Activity, permission: String): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
    }

    private fun requestPermissions(
        code: Int,
        listener: PermissionListener?,
        vararg permission: String?
    ) {
        this.listener = listener
        this.rQCodeChange = code
        if (permission.isEmpty()) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(activity, permission, code)
        } else {
            listener?.onPermissionAllow()
        }
    }

    fun onRequestPermissionsResult(requestCode: Int) {
        if (requestCode == RQ_CODE_READ_EXTERNAL_STORAGE) {
            if (isPermissionReadWriteStorage()) {
                listener?.onPermissionAllow()
            } else if (isPermissionRationaleReadWriteStorage(activity)) {
                listener?.onPermissionDenied()
            } else {
                listener?.onPermissionAskAgain()
            }
        }
    }

    fun onResume(): Boolean {
        if (this.rQCodeSetting == -1) {
            return false
        }
        return true
    }

    fun disOnResume() {
        this.rQCodeSetting = -1
    }

    fun openAppSettings(code: Int) {
        postHandler()
        this.rQCodeSetting = code
        val myAppSettings = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + activity.packageName)
        )
        myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
        myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        kotlin.runCatching {
            activity.startActivity(myAppSettings)
        }
    }
}