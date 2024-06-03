package app.benaya.mystory.data.local.repository

import app.benaya.mystory.data.local.StoryEntity
import app.benaya.mystory.data.local.database.EntityStoriesDatabase
import app.benaya.mystory.data.responses.ResponseLogin
import app.benaya.mystory.data.responses.ResponseRegister
import app.benaya.mystory.data.responses.ResponseStories
import app.benaya.mystory.data.responses.ResponseStoriesUpload
import app.benaya.mystory.data.retrofit.ApiConfig
import app.benaya.mystory.data.retrofit.ApiService
import app.benaya.mystory.paging.StoriesRemoteMediator
import app.benaya.mystory.settings.wrapEspressoIdlingResource
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoryEntityRepository(
    private val entityStoriesDatabase: EntityStoriesDatabase,
    private val apiService: ApiService
) {

    private var listStoryLocation = MutableLiveData<List<StoryEntity>>()
    var listStory: LiveData<List<StoryEntity>> = listStoryLocation
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoadingStory: LiveData<Boolean> = _isLoading
    private val _storyStatus = MutableLiveData<String>()
    val storyStatus: LiveData<String> = _storyStatus
    var isErrorStory: Boolean = false

    private val _isLoadingRegister = MutableLiveData<Boolean>()
    val isLoadingRegister: LiveData<Boolean> = _isLoadingRegister
    private val _registerStatus = MutableLiveData<String>()
    val registerStatus: LiveData<String> = _registerStatus
    var isErrorRegister: Boolean = false

    private val _isLoadingLogin = MutableLiveData<Boolean>()
    val isLoadingLogin: LiveData<Boolean> = _isLoadingLogin
    private val _loginStatus = MutableLiveData<String>()
    val loginStatus: LiveData<String> = _loginStatus
    var isErrorLogin: Boolean = false
    private val _login = MutableLiveData<ResponseLogin>()
    val loginUser: LiveData<ResponseLogin> = _login

    private val _uploadstorystatus = MutableLiveData<String>()
    val uploadstorystatus: LiveData<String> = _uploadstorystatus
    private val _isLoadinguploadStory = MutableLiveData<Boolean>()
    val isLoadinguploadStory: LiveData<Boolean> = _isLoadinguploadStory
    var isErrorstoryupload: Boolean = false

    fun register(name: String, email: String, password: String) {
        _isLoadingRegister.value = true
        val api = ApiConfig.getApiService().register(name, email, password)
        api.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                _isLoadingRegister.value = false
                if (response.isSuccessful) {
                    isErrorRegister = false
                    _registerStatus.value = "Account successfully created"
                } else {
                    isErrorRegister = true
                    when (response.code()) {
                        400 -> _registerStatus.value =
                            "Email is already taken"

                        408 -> _registerStatus.value =
                            "Your internet connection is slow, please try again"

                        500 -> _registerStatus.value =
                            "Server not found, please try again later"

                        else -> {
                            _registerStatus.value = response.message()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                isErrorRegister = true
                _isLoadingRegister.value = false
                _registerStatus.value = t.message.toString()
            }

        })
    }

    fun login(email: String, password: String) {
        wrapEspressoIdlingResource {
            _isLoadingLogin.value = true
            val api = ApiConfig.getApiService().login(email, password)
            api.enqueue(object : Callback<ResponseLogin> {
                override fun onResponse(
                    call: Call<ResponseLogin>,
                    response: Response<ResponseLogin>
                ) {
                    val responseBody = response.body()
                    _isLoadingLogin.value = false
                    if (response.isSuccessful) {
                        isErrorLogin = false
                        _login.value = responseBody!!
                        _loginStatus.value =
                            "Welcome ${_login.value!!.loginResult.name}, To Story Apps"
                    } else {
                        isErrorLogin = true
                        when (response.code()) {
                            401 -> _loginStatus.value = "User not found"
                            500 -> _loginStatus.value = "Server not found. Please try again later."
                            else -> {
                                _loginStatus.value = response.message()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                    isErrorLogin = true
                    _isLoadingLogin.value = false
                    _loginStatus.value = t.message.toString()
                }

            })
        }
    }

    fun getStory(token: String) {
        _isLoading.value = true
        val api = ApiConfig.getApiService().getStoryList(32, 1, "Bearer $token")
        api.enqueue(object : Callback<ResponseStories> {
            override fun onResponse(
                call: Call<ResponseStories>,
                response: Response<ResponseStories>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    isErrorStory = false
                    val responseBody = response.body()
                    if (responseBody != null) {
                        listStoryLocation.value = responseBody.listStory
                    }
                    _storyStatus.value = responseBody?.message.toString()

                } else {
                    isErrorStory = true
                    _storyStatus.value = response.message()
                }
            }

            override fun onFailure(call: Call<ResponseStories>, t: Throwable) {
                _isLoading.value = false
                isErrorStory = true
                _storyStatus.value = t.message.toString()
            }
        })
    }

    fun uploadStory(
        token: String,
        photo: MultipartBody.Part,
        caption: RequestBody,
        lat: Double?,
        lng: Double?
    ) {
        _isLoadinguploadStory.value = true
        val service = ApiConfig.getApiService().uploadStory(
            "Bearer $token", photo, caption, lat?.toFloat(), lng?.toFloat()
        )
        service.enqueue(object : Callback<ResponseStoriesUpload> {
            override fun onResponse(
                call: Call<ResponseStoriesUpload>,
                response: Response<ResponseStoriesUpload>
            ) {
                _isLoadinguploadStory.value = false
                if (response.isSuccessful) {
                    isErrorstoryupload = false
                    val responseBody = response.body()
                    if (responseBody != null && !responseBody.error) {
                        _uploadstorystatus.value = responseBody.message
                    }
                } else {
                    isErrorstoryupload = true
                    _uploadstorystatus.value = response.message()

                }
            }

            override fun onFailure(call: Call<ResponseStoriesUpload>, t: Throwable) {
                _isLoadinguploadStory.value = false
                isErrorstoryupload = true
                _uploadstorystatus.value = t.message
            }
        })
    }

    @ExperimentalPagingApi
    fun getPagingStory(token: String): LiveData<PagingData<StoryEntity>> {
        val pager = Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(entityStoriesDatabase, apiService, token),
            pagingSourceFactory = {
                entityStoriesDatabase.storyentityDao().getAllPagingStory()
            }
        )
        return pager.liveData
    }

}