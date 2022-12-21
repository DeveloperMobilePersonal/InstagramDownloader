package instagram.photo.video.downloader.story.saver.app

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import com.androidnetworking.AndroidNetworking
import instagram.photo.video.downloader.story.saver.module.listModule
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class InDownloaderApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ContextGlobal.set(applicationContext)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        startKoin {
            androidContext(applicationContext)
            modules(listModule)
        }
        /*val builder = RequestConfiguration.Builder()
        builder.setTestDeviceIds(
            listOf("519E8BF3AF97EB4B96C4A3F9A143DBE5")
        )
        MobileAds.setRequestConfiguration(builder.build())*/
        val okHttpClient = OkHttpClient.Builder()
            .writeTimeout(10000, TimeUnit.MILLISECONDS)
            .readTimeout(10000, TimeUnit.MILLISECONDS)
            .connectTimeout(10000, TimeUnit.MILLISECONDS)
            .retryOnConnectionFailure(true)
            .build()
        AndroidNetworking.initialize(this, okHttpClient)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}