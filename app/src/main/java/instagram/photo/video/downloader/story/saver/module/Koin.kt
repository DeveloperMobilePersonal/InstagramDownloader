package video.photo.instagram.downloader.story.module

import android.app.Activity
import androidx.activity.ComponentActivity
import instagram.photo.video.downloader.story.saver.module.listModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.scope.LifecycleScopeDelegate
import org.koin.androidx.scope.activityScope
import org.koin.core.Koin
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin

private fun getKoin(activity: ComponentActivity): Koin {
    return if (activity is KoinComponent) {
        activity.getKoin()
    } else {
        GlobalContext.getOrNull() ?: startKoin {
            androidContext(activity)
            modules(listModule)
        }.koin
    }
}

fun ComponentActivity.contextAwareActivityScope() = runCatching {
    LifecycleScopeDelegate<Activity>(
        lifecycleOwner = this,
        koin = getKoin(this)
    )
}.getOrElse { activityScope() }