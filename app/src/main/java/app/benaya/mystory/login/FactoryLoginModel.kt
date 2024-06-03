package app.benaya.mystory.login

import app.benaya.mystory.settings.ViewModelAuthentication
import app.benaya.mystory.settings.PreferencesForSetting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FactoryLoginModel(private val prefen: PreferencesForSetting) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewModelAuthentication::class.java)) {
            return ViewModelAuthentication(prefen) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}