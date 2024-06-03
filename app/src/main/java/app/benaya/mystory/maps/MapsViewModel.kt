package app.benaya.mystory.maps

import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.repository.StoryEntityRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class MapsViewModel(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val stories: LiveData<List<StoryEntity>> = storyEntityRepository.listStory

    val isLoadingStory: LiveData<Boolean> = storyEntityRepository.isLoadingStory

    val storyStatus: LiveData<String> = storyEntityRepository.storyStatus

    var isErrorStory: Boolean = false

    init {
        storyStatus.observeForever { status ->
            isErrorStory = status != "Stories fetched successfully"
        }
    }

    fun getListStoryLocation(token: String) {
        storyEntityRepository.getStory(token)
    }
}