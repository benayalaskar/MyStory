package app.benaya.mystory.story

import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.repository.StoryEntityRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn


class ViewModelStories(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    @ExperimentalPagingApi
    fun getPagingStory(token: String): LiveData<PagingData<StoryEntity>> {
        return storyEntityRepository.getPagingStory(token).cachedIn(viewModelScope)
    }
}