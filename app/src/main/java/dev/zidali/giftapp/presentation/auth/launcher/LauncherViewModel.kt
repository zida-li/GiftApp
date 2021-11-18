package dev.zidali.giftapp.presentation.auth.launcher

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel
@Inject
constructor(

): ViewModel() {

    val state: MutableLiveData<LauncherState> = MutableLiveData(LauncherState())

    fun onTriggerEvent(event: LauncherEvents) {
        when(event) {
            is LauncherEvents.LoginWithGoogle -> {
                loginWithGoogle(event.token)
            }
        }
    }

    private fun loginWithGoogle(token: String) {
        TODO("Not yet implemented")
    }

}