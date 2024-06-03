package app.benaya.mystory.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import app.benaya.mystory.adapters.StoriesAdapter
import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.repository.StoryEntityRepository
import app.benaya.mystory.story.ViewModelStories
import app.benaya.mystory.utils.DataDummy.generateDummyNewStory
import app.benaya.mystory.utils.MainDispatcherRule
import app.benaya.mystory.utils.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

@Suppress("DEPRECATION")
@ExperimentalCoroutinesApi
class StoryListPagingTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()

    @Mock
    private lateinit var storyEntityRepository: StoryEntityRepository
    private lateinit var viewModelStories: ViewModelStories

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        viewModelStories = ViewModelStories(storyEntityRepository)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `when pagingstory success and not null`() = runTest {
        val dummyStory = generateDummyNewStory()
        val data: PagingData<StoryEntity> = StoryListPagingSource.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<StoryEntity>>()
        val token = "token"
        story.value = data

        `when`(storyEntityRepository.getPagingStory(token)).thenReturn(story)
        val actualData: PagingData<StoryEntity> =
            viewModelStories.getPagingStory(token).getOrAwaitValue()

        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback(),
            updateCallback = noopListCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        diff.submitData(actualData)

        assertNotNull(diff.snapshot())
        assertEquals(dummyStory.size, diff.snapshot().size)
        assertEquals(dummyStory[0], diff.snapshot()[0])
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `when pagingstory failure due to empty data`() = runTest {
        val story = MutableLiveData<PagingData<StoryEntity>>()
        val data: PagingData<StoryEntity> = PagingData.from(emptyList())
        val token = "token"
        story.value = data

        `when`(storyEntityRepository.getPagingStory(token)).thenReturn(story)

        val actualData: PagingData<StoryEntity> =
            viewModelStories.getPagingStory(token).getOrAwaitValue()

        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback(),
            updateCallback = noopListCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        diff.submitData(actualData)

        assertEquals(0, diff.snapshot().size)
    }

    private val noopListCallback = object : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
    }

    class StoryListPagingSource private constructor() :
        PagingSource<Int, LiveData<List<StoryEntity>>>() {
        companion object {
            fun snapshot(items: List<StoryEntity>): PagingData<StoryEntity> {
                return PagingData.from(items)
            }
        }

        override fun getRefreshKey(state: PagingState<Int, LiveData<List<StoryEntity>>>): Int {
            return 0
        }

        override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<StoryEntity>>> {
            return LoadResult.Page(emptyList(), 0, 1)
        }
    }
}