package dev.zidali.giftapp.presentation.auth.login

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.auth.LoginWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.shared.GetEmail
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
@Inject
constructor(
    private val getEmail: GetEmail,
    private val appDataStore: AppDataStore,
    private val loginWithEmailAndPassword: LoginWithEmailAndPassword,
    private val sessionManager: SessionManager,
): ViewModel() {

    val state: MutableLiveData<LoginState> = MutableLiveData(LoginState())

    init {
        onTriggerEvent(LoginEvents.GetEmail)
    }

    fun onTriggerEvent(event: LoginEvents) {
        when(event) {
            is LoginEvents.GetEmail -> {
                getEmail()
            }
            is LoginEvents.LoginWithGoogle -> {
                loginWithGoogle()
            }
            is LoginEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is LoginEvents.OnUpdatePassword -> {
                onUpdatePassword(event.password)
            }
            is LoginEvents.SaveLoginState -> {
                saveLoginState()
            }
            is LoginEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is LoginEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun getEmail() {
        state.value?.let { state->
            getEmail.execute(
                loginFragment = true,
                registerFragment = false,
            ).onEach { dataState ->

                dataState.data?.let { email ->
                    this.state.value = state.copy(login_email = email.email)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message.equals(ErrorHandling.ERROR_NO_EMAIL_SAVED)){
                        Log.d(Constants.TAG, ErrorHandling.ERROR_NO_EMAIL_SAVED)
                    } else {
                        Log.d(Constants.TAG, "${stateMessage.response.message}")
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun loginWithGoogle() {
        state.value?.let {state->
            val loginError = LoginState(
                state.login_email,
                state.login_password
            ).isValidForRegistration()
            if(loginError == LoginState.LoginError.none()) {
                loginWithEmailAndPassword.execute(
                    email = state.login_email!!,
                    password = state.login_password!!,
                ).onEach { dataState ->

                    dataState.data?.let { accountProperties ->
                        sessionManager.onTriggerEvent(SessionEvents.Login(accountProperties.accountProperties!!))
                    }

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }

                }.launchIn(viewModelScope)
            } else {
                appendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = loginError,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error
                        )
                    )
                )
            }
        }
    }

    private fun onUpdateEmail(email: String) {
        state.value?.let { state->
            this.state.value = state.copy(login_email = email)
        }
    }

    private fun onUpdatePassword(password: String) {
        state.value?.let { state->
            this.state.value = state.copy(login_password = password)
        }
    }

    private fun saveLoginState () {
        viewModelScope.launch {
            appDataStore.setValue(DataStoreKeys.LOGIN_EMAIL, state.value?.login_email!!)
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