package dev.zidali.giftapp.presentation.auth.launcher

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.interactors.auth.LoginWithGoogle
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel
@Inject
constructor(
    private val loginWithGoogle: LoginWithGoogle,
    private val sessionManager: SessionManager,
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
        state.value?.let {
            loginWithGoogle.execute(
                idToken = token
            ).onEach { dataState ->

                dataState.data?.let { accountProperty->
                    sessionManager.onTriggerEvent(SessionEvents.Login(accountProperty.accountProperties!!))
                }

            }.launchIn(viewModelScope)


        }
    }

}