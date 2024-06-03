package app.benaya.mystory

import app.benaya.mystory.di.Injection
import app.benaya.mystory.login.ModelLoginView
import app.benaya.mystory.maps.MapsViewModel
import app.benaya.mystory.register.ViewModelRegister
import app.benaya.mystory.story.ViewModelStoriesUpload
import app.benaya.mystory.story.ViewModelStories
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


class PagingStoryModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelStories::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelStories(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class RegisterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelRegister::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelRegister(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ModelLoginView::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ModelLoginView(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class StoryUploadModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelStoriesUpload::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewModelStoriesUpload(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MapsModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MapsViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

