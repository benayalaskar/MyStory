package app.benaya.mystory.login

import app.benaya.mystory.data.local.repository.StoryEntityRepository
import app.benaya.mystory.data.responses.ResponseLogin
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ModelLoginView(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val loginStatus: LiveData<String> = storyEntityRepository.loginStatus

    val isLoadingLogin: LiveData<Boolean> = storyEntityRepository.isLoadingLogin

    var isErrorLogin: Boolean = false

    init {
        loginStatus.observeForever { status ->
            isErrorLogin = status != "Welcome ${login.value?.loginResult?.name}, To Story Apps"
        }
    }

    val login: LiveData<ResponseLogin> = storyEntityRepository.loginUser

    fun login(email: String, password: String) {
        storyEntityRepository.login(email, password)
    }
}
