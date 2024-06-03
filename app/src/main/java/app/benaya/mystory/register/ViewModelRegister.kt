package app.benaya.mystory.register

import app.benaya.mystory.data.local.repository.StoryEntityRepository
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ViewModelRegister(private val storyEntityRepository: StoryEntityRepository) : ViewModel() {

    val registerStatus: LiveData<String> = storyEntityRepository.registerStatus

    val isLoadingRegister: LiveData<Boolean> = storyEntityRepository.isLoadingRegister

    var isErrorRegister: Boolean = false

    init {
        registerStatus.observeForever { status ->
            isErrorRegister = status != "Account successfully created! Lets login!"
        }
    }

    fun register(name: String, email: String, password: String) {
        storyEntityRepository.register(name, email, password)
    }
}