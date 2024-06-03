package app.benaya.mystory.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ViewModelAuthentication(private val prefen: PreferencesForSetting) : ViewModel() {

    fun getUserLoginSession(): LiveData<Boolean> {
        return prefen.getUserLoginSession().asLiveData()
    }

    fun saveUserLoginSession(userloginSession: Boolean) {
        viewModelScope.launch {
            prefen.saveUserLoginSession(userloginSession)
        }
    }

    fun getUserLoginToken(): LiveData<String> {
        return prefen.getUserLoginToken().asLiveData()
    }

    fun saveUserLoginToken(token: String) {
        viewModelScope.launch {
            prefen.saveUserLoginToken(token)
        }
    }

    fun getUserLoginName(): LiveData<String> {
        return prefen.getUserloginName().asLiveData()
    }

    fun saveUserLoginName(token: String) {
        viewModelScope.launch {
            prefen.saveUserLoginName(token)
        }
    }

    fun getUserLoginUid(): LiveData<String> {
        return prefen.getUserloginUid().asLiveData()
    }

    fun saveUserLoginUid(token: String) {
        viewModelScope.launch {
            prefen.saveUserLoginUid(token)
        }
    }

    fun getUserLoginEmail(): LiveData<String> {
        return prefen.getUserloginEmail().asLiveData()
    }

    fun saveUserLoginEmail(token: String) {
        viewModelScope.launch {
            prefen.saveUserLoginEmail(token)
        }
    }

    fun getUserLastLoginSession(): LiveData<String> {
        return prefen.getUserLastLoginSession().asLiveData()
    }

    fun saveUserLastLoginSession(token: String) {
        viewModelScope.launch {
            prefen.saveUserLastLoginSession(token)
        }
    }

    fun deleteUserLoginSession() {
        viewModelScope.launch {
            prefen.deleteUserLoginSession()
        }
    }

}