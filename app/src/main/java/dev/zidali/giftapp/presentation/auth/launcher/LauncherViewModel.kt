package dev.zidali.giftapp.presentation.auth.launcher

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.auth.LoginWithGoogle
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import dev.zidali.giftapp.util.Constants
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
            is LauncherEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is LauncherEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
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

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)

        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(stateMessage.response.uiComponentType !is UIComponentType.None){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onRemoveHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(Constants.TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

}