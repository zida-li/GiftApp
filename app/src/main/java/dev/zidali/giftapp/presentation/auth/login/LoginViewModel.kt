package dev.zidali.giftapp.presentation.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(

): ViewModel() {

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())

    fun onTriggerEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.LoginWithGoogle -> {
                registerWithGoogle(event.email, event.password)
            }
        }
    }

    private fun registerWithGoogle(email: String, password: String) {
        TODO("Not yet implemented")
    }

}