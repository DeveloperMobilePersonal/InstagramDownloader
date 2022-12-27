package instagram.photo.video.downloader.story.saver.ui.splash

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flow

class JobSplash {

    private var progress = -1
    private val max = 100
    private var isShowAds = false
    private var delay = 20L
    private var loopingFlowJob: Job? = null
    private var loopingFlow = flow {
        while (true) {
            emit(Unit)
            delay(delay)
        }
    }

    fun setDelay(delay: Long) {
        this.delay = delay
    }

    fun startJob(coroutineScope: CoroutineScope,jobProgress: JobProgress?) {
        if (isShowingAd()) {
            return
        }
        stopJob()
        if (progress >= max) {
            jobProgress?.onProgress(progress)
            return
        }
        loopingFlowJob = coroutineScope.launch(Dispatchers.Main) {
            loopingFlow.collect {
                progress++
                if (progress >= max) {
                    stopJob()
                }
                jobProgress?.onProgress(progress)
            }
        }
    }

    fun stopJob() {
        loopingFlowJob?.cancel()
    }

    fun isShowingAd(): Boolean {
        return isShowAds
    }

    fun isProgressMax(): Boolean {
        return progress >= max
    }

    fun setShowAds() {
        isShowAds = true
    }

    interface JobProgress {
        fun onProgress(count: Int)
    }

}