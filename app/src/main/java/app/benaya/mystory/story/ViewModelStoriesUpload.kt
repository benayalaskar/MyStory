package app.benaya.mystory.story


import app.benaya.mystory.data.local.repository.StoryEntityRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ViewModelStoriesUpload(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val uploadstorystatus: LiveData<String> = storyEntityRepository.uploadstorystatus

    val isLoadinguploadStory: LiveData<Boolean> = storyEntityRepository.isLoadinguploadStory

    var isErrorstoryupload: Boolean = false

    init {
        uploadstorystatus.observeForever { status ->
            isErrorstoryupload = status != "Story successfully created!"
        }
    }

    fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        caption: RequestBody,
        lat: Double?,
        lng: Double?,
    ) {
        storyEntityRepository.uploadStory(token, photo, caption, lat, lng)
    }
}