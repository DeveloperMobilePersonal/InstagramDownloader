package instagram.photo.video.downloader.story.saver.ui.gallery

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import instagram.photo.video.downloader.story.saver.data.scanMedia.GalleryModel
import instagram.photo.video.downloader.story.saver.data.scanMedia.MediaStoreService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GalleryViewModel(private val mediaStoreService: MediaStoreService) : ViewModel() {

    val liveCurrentListGallery = MutableLiveData<List<GalleryModel>>()
    val isLoading = MutableLiveData(true)
    val isEmpty = MutableLiveData(false)

    fun fetch() {
        viewModelScope.launch(Dispatchers.IO) {
            val list = mediaStoreService.scan()
            isLoading.postValue(false)
            isEmpty.postValue(list.isEmpty())
            liveCurrentListGallery.postValue(list)
        }
    }
}