package io.stawallet.signer.welcome

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.stawallet.signer.R
import io.stawallet.signer.data.*
import io.stawallet.signer.helper.Event
import io.stawallet.signer.helper.coRunMain
import io.stawallet.signer.helper.unjustifiedSilence
import retrofit2.HttpException

class WelcomeViewModel : ViewModel() {

    val currentPage: MutableLiveData<String> = MutableLiveData()
    val loginResult = MutableLiveData<Event<Any?>>()

    fun dismissSplash() {
        if (sessionManager.isLoggedIn()) {
            currentPage.postValue("unlock")
        } else {
            currentPage.postValue("login")
        }

    }

    private val _loginForm = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginForm

    fun login(email: String, password: String) {
        try {
            coRunMain {
                loginResult.postValue(Event(stawalletApiClient.login(email, password).await()))
            }
        } catch (e: HttpException) {
            unjustifiedSilence {
                loginResult.postValue(Event(e.reason() ?: e.code()))
            }
        } catch (e: Exception) {
            unjustifiedSilence {
                loginResult.postValue(Event(null))
            }
        }
    }

    fun loginDataChanged(username: String, password: String) {
        if (!isUserNameValid(username)) {
            _loginForm.value =
                LoginFormState(usernameError = R.string.invalid_username)
        } else if (!isPasswordValid(password)) {
            _loginForm.value =
                LoginFormState(passwordError = R.string.invalid_password)
        } else {
            _loginForm.value =
                LoginFormState(isDataValid = true)
        }
    }

    // A placeholder username validation check
    private fun isUserNameValid(username: String): Boolean {
        return if (username.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    private fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }

    fun hardLogout() {
        // TODO: Implement
        unjustifiedSilence { sessionManager.whipSession() }
        unjustifiedSilence { AppProtection.whip() }
        unjustifiedSilence { stawalletDatabase.wipeDb() }
    }

}

data class LoginFormState(
    val usernameError: Int? = null,
    val passwordError: Int? = null,
    val isDataValid: Boolean = false
)

