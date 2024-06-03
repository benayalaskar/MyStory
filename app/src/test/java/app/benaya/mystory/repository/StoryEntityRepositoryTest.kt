package app.benaya.mystory.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.*
import androidx.recyclerview.widget.ListUpdateCallback
import app.benaya.mystory.adapters.StoriesAdapter
import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.repository.StoryEntityRepository
import app.benaya.mystory.data.responses.ResponseLogin
import app.benaya.mystory.utils.DataDummy.generateDummyNewStory
import app.benaya.mystory.utils.DataDummy.generateDummyLoginResponse
import app.benaya.mystory.utils.DataDummy.generateDummyLoginResponseFailed
import app.benaya.mystory.utils.DataDummy.generateDummyNewStoryFailed
import app.benaya.mystory.utils.DataDummy.generateDummyRegisterFailed
import app.benaya.mystory.utils.DataDummy.generateDummyStoryUploadFailed
import app.benaya.mystory.utils.MainDispatcherRule
import app.benaya.mystory.utils.getOrAwaitValue
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.io.File

class StoryEntityRepositoryTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainDispatcherRule = MainDispatcherRule()

    private lateinit var storyEntityRepository: StoryEntityRepository
    private val dummyEmail = "benaya77@gmail.com"
    private val dummyPass = "12345678"
    private val dummyName = "Benaya Testing"

    @Mock
    private var mockFile = File("MockFile")

    @Before
    fun setUp() {
        storyEntityRepository = Mockito.mock(StoryEntityRepository::class.java)
    }

    @Test
    fun `when try login request`() {
        val dummyResponseLogin = generateDummyLoginResponse()

        val expectedResponseLogin = MutableLiveData<ResponseLogin>()
        expectedResponseLogin.value = dummyResponseLogin

        storyEntityRepository.login(dummyEmail, dummyPass)

        Mockito.verify(storyEntityRepository).login(dummyEmail, dummyPass)
        `when`(storyEntityRepository.loginUser).thenReturn(expectedResponseLogin)

        val actualData = storyEntityRepository.loginUser.getOrAwaitValue()
        Mockito.verify(storyEntityRepository).loginUser
        assertNotNull(actualData)
        assertEquals(expectedResponseLogin.value, actualData)
    }

    @Test
    fun `when login failure`() {
        val dummyLoginResponse = generateDummyLoginResponseFailed()

        val expectedLogin = MutableLiveData<ResponseLogin>()
        expectedLogin.value = dummyLoginResponse

        `when`(storyEntityRepository.loginUser).thenReturn(expectedLogin)
        val actualLoginResponse = storyEntityRepository.loginUser.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).loginUser
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `when login success`() {
        val dummyLoginResponse = generateDummyLoginResponse()

        val expectedLogin = MutableLiveData<ResponseLogin>()
        expectedLogin.value = dummyLoginResponse

        `when`(storyEntityRepository.loginUser).thenReturn(expectedLogin)
        val actualLoginResponse = storyEntityRepository.loginUser.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).loginUser
        assertNotNull(actualLoginResponse)
        assertEquals(expectedLogin.value, actualLoginResponse)
    }

    @Test
    fun `when register success and not null`() {
        val expectedRegisterMessage = MutableLiveData<String>()
        expectedRegisterMessage.value = "User Created Successfully"

        storyEntityRepository.register(dummyName, dummyEmail, dummyPass)

        Mockito.verify(storyEntityRepository).register(dummyName, dummyEmail, dummyPass)
        `when`(storyEntityRepository.registerStatus).thenReturn(expectedRegisterMessage)

        val actualData = storyEntityRepository.registerStatus.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).registerStatus
        assertNotNull(actualData)
        assertEquals(expectedRegisterMessage.value, actualData)
    }

    @Test
    fun `when register failure due to existing email`() {
        val dummyRegisterResponse = generateDummyRegisterFailed()
        val expectedErrorMessage = MutableLiveData<String>()
        expectedErrorMessage.value = dummyRegisterResponse.message

        storyEntityRepository.register(dummyName, dummyEmail, dummyPass)

        Mockito.verify(storyEntityRepository).register(dummyName, dummyEmail, dummyPass)
        `when`(storyEntityRepository.registerStatus).thenReturn(expectedErrorMessage)

        val actualErrorMessage = storyEntityRepository.registerStatus.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).registerStatus
        assertNotNull(actualErrorMessage)
        assertEquals(expectedErrorMessage.value, actualErrorMessage)
    }

    @Test
    fun `when uploadstory success`() {
        val expectedUploadStory = MutableLiveData<String>()
        expectedUploadStory.value = "Story Uploaded Successfully"

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val caption: RequestBody = "Caption Anda".toRequestBody("text/plain".toMediaType())
        val token = "token"
        val latlng = LatLng(35.6586, 139.7454)

        storyEntityRepository.uploadStory(
            token,
            imageMultipart,
            caption,
            latlng.latitude,
            latlng.longitude
        )

        Mockito.verify(storyEntityRepository).uploadStory(
            token,
            imageMultipart,
            caption,
            latlng.latitude,
            latlng.longitude
        )

        `when`(storyEntityRepository.uploadstorystatus).thenReturn(expectedUploadStory)

        val actualRegisterMessage = storyEntityRepository.uploadstorystatus.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).uploadstorystatus
        assertNotNull(actualRegisterMessage)
        assertEquals(expectedUploadStory.value, actualRegisterMessage)
    }

    @Test
    fun `when uploadstory failure`() {
        val dummyStoryUpload = generateDummyStoryUploadFailed()
        val expectedErrorMessage = MutableLiveData<String>()
        expectedErrorMessage.value = dummyStoryUpload.message

        val requestImageFile = mockFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            "fileName",
            requestImageFile
        )
        val caption: RequestBody = "Caption Anda".toRequestBody("text/plain".toMediaType())
        val token = "token"
        val latlng = LatLng(35.6586, 139.7454)

        storyEntityRepository.uploadStory(
            token,
            imageMultipart,
            caption,
            latlng.latitude,
            latlng.longitude
        )

        Mockito.verify(storyEntityRepository).uploadStory(
            token,
            imageMultipart,
            caption,
            latlng.latitude,
            latlng.longitude
        )

        `when`(storyEntityRepository.uploadstorystatus).thenReturn(expectedErrorMessage)

        val actualErrorMessage = storyEntityRepository.uploadstorystatus.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).uploadstorystatus
        assertNotNull(actualErrorMessage)
        assertEquals(expectedErrorMessage.value, actualErrorMessage)
    }


    @Test
    fun `when try getstory request sucess`() {
        val dummyStory = generateDummyNewStory()
        val expectedStory = MutableLiveData<List<StoryEntity>>()
        expectedStory.value = dummyStory

        val token = "token"
        storyEntityRepository.getStory(token)
        Mockito.verify(storyEntityRepository).getStory(token)

        `when`(storyEntityRepository.listStory).thenReturn(expectedStory)

        val actualStories = storyEntityRepository.listStory.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).listStory

        assertNotNull(actualStories)
        assertEquals(expectedStory.value, actualStories)
        assertEquals(dummyStory.size, actualStories.size)
    }

    @Test
    fun `when getstory success and not null`() {
        val dummyStory = generateDummyNewStory()
        val expectedStory = MutableLiveData<List<StoryEntity>>()
        expectedStory.value = dummyStory

        `when`(storyEntityRepository.listStory).thenReturn(expectedStory)

        val actualStories = storyEntityRepository.listStory.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).listStory

        assertNotNull(actualStories)
        assertEquals(expectedStory.value, actualStories)
        assertEquals(dummyStory.size, actualStories.size)
        assertEquals(dummyStory[0].id, actualStories[0].id)
    }

    @Test
    fun `when getstory failed and empty`() {
        val dummyStory = generateDummyNewStoryFailed()
        val expectedStory = MutableLiveData<List<StoryEntity>>()
        expectedStory.value = dummyStory

        `when`(storyEntityRepository.listStory).thenReturn(expectedStory)

        val actualStories = storyEntityRepository.listStory.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).listStory

        assertNotNull(actualStories)
        assertEquals(0, actualStories.size)
    }


    @Test
    fun `when loading register success`() {
        val expectedLoading = MutableLiveData<Boolean>()
        expectedLoading.value = true

        `when`(storyEntityRepository.isLoadingRegister).thenReturn(expectedLoading)

        val actualLoading = storyEntityRepository.isLoadingRegister.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).isLoadingRegister
        assertNotNull(actualLoading)
        assertEquals(expectedLoading.value, actualLoading)
    }

    @Test
    fun `when loading login success`() {
        val expectedLoading = MutableLiveData<Boolean>()
        expectedLoading.value = true

        `when`(storyEntityRepository.isLoadingLogin).thenReturn(expectedLoading)

        val actualLoading = storyEntityRepository.isLoadingLogin.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).isLoadingLogin
        assertNotNull(actualLoading)
        assertEquals(expectedLoading.value, actualLoading)
    }

    @Test
    fun `when loading uploadstory success`() {
        val expectedLoading = MutableLiveData<Boolean>()
        expectedLoading.value = true

        `when`(storyEntityRepository.isLoadinguploadStory).thenReturn(expectedLoading)

        val actualLoading = storyEntityRepository.isLoadinguploadStory.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).isLoadinguploadStory
        assertNotNull(actualLoading)
        assertEquals(expectedLoading.value, actualLoading)
    }

    @Test
    fun `when loading getstory success`() {
        val expectedLoading = MutableLiveData<Boolean>()
        expectedLoading.value = true

        `when`(storyEntityRepository.isLoadingStory).thenReturn(expectedLoading)

        val actualLoading = storyEntityRepository.isLoadingStory.getOrAwaitValue()

        Mockito.verify(storyEntityRepository).isLoadingStory
        assertNotNull(actualLoading)
        assertEquals(expectedLoading.value, actualLoading)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `when pagingstory success and not null`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val dummyStory = generateDummyNewStory()
        val data = StoryListPagingSource.snapshot(dummyStory)
        val story = MutableLiveData<PagingData<StoryEntity>>()
        val token = "token"
        story.value = data

        `when`(storyEntityRepository.getPagingStory(token)).thenReturn(story)

        val actualData = storyEntityRepository.getPagingStory(token).getOrAwaitValue()

        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        diff.submitData(actualData)


        advanceUntilIdle()
        Mockito.verify(storyEntityRepository).getPagingStory(token)
        assertNotNull(diff.snapshot())
        assertEquals(dummyStory.size, diff.snapshot().size)
        assertEquals(dummyStory[0].name, diff.snapshot()[0]?.name)
    }

    @ExperimentalCoroutinesApi
    @ExperimentalPagingApi
    @Test
    fun `when pagingstory failure due to empty data`() = runTest {
        val noopListUpdateCallback = NoopListCallback()
        val story = MutableLiveData<PagingData<StoryEntity>>()
        val token = "token"
        story.value = PagingData.empty()

        `when`(storyEntityRepository.getPagingStory(token)).thenReturn(story)

        val actualData = storyEntityRepository.getPagingStory(token).getOrAwaitValue()

        val diff = AsyncPagingDataDiffer(
            diffCallback = StoriesAdapter.DiffCallback(),
            updateCallback = noopListUpdateCallback,
            mainDispatcher = Dispatchers.Unconfined,
            workerDispatcher = Dispatchers.Unconfined,
        )
        diff.submitData(actualData)

        advanceUntilIdle()
        Mockito.verify(storyEntityRepository).getPagingStory(token)
        assertNotNull(diff.snapshot())
        assertEquals(0, diff.snapshot().size)
    }

    class NoopListCallback : ListUpdateCallback {
        override fun onChanged(position: Int, count: Int, payload: Any?) {}
        override fun onMoved(fromPosition: Int, toPosition: Int) {}
        override fun onInserted(position: Int, count: Int) {}
        override fun onRemoved(position: Int, count: Int) {}
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