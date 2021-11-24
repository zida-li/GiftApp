package dev.zidali.giftapp.presentation.auth.register

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.auth.RegisterWithEmailAndPassword
import dev.zidali.giftapp.business.interactors.auth.shared.GetEmail
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject
constructor(
    private val appDataStore: AppDataStore,
    private val getEmail: GetEmail,
    private val registerWithEmailAndPassword: RegisterWithEmailAndPassword,
    private val sessionManager: SessionManager,
): ViewModel() {

    val state: MutableLiveData<RegisterState> = MutableLiveData(RegisterState())

    init {
        onTriggerEvent(RegisterEvents.GetEmail)
    }

    fun onTriggerEvent(event: RegisterEvents) {
        when(event) {
            is RegisterEvents.RegisterWithGoogle -> {
                registerWithGoogle()
            }
            is RegisterEvents.OnUpdateEmail -> {
                onUpdateEmail(event.email)
            }
            is RegisterEvents.OnUpdatePassword -> {
                onUpdatePassword(event.password)
            }
            is RegisterEvents.OnUpdateConfirmPassword -> {
                onUpdateConfirmPassword(event.confirmPassword)
            }
            is RegisterEvents.SaveRegisterState -> {
                saveRegisterState()
            }
            is RegisterEvents.GetEmail -> {
                getEmail()
            }
            is RegisterEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is RegisterEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun registerWithGoogle() {
        state.value?.let {state->
            val registrationError = RegisterState(
                state.registration_email,
                state.registration_password,
                state.registration_confirm_password,
            ).isValidForRegistration()
            if (registrationError == RegisterState.RegistrationError.none()) {
                registerWithEmailAndPassword.execute(
                    state.registration_email,
                    state.registration_password,
                ).onEach { dataState ->

                    this.state.value = state.copy(isLoading = dataState.isLoading)

                    dataState.data?.let {accountProperties->
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
                            message = registrationError,
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
            this.state.value = state.copy(registration_email = email)
        }
    }

    private fun onUpdatePassword(password: String) {
        state.value?.let { state->
            this.state.value = state.copy(registration_password = password)
        }
    }

    private fun onUpdateConfirmPassword(password: String) {
        state.value?.let { state->
            this.state.value = state.copy(registration_confirm_password = password)
        }
    }

    private fun saveRegisterState() {
        viewModelScope.launch {
            appDataStore.setValue(DataStoreKeys.REGISTRATION_EMAIL, state.value!!.registration_email)
        }
    }

    private fun getEmail() {
        state.value?.let { state->

            getEmail.execute(
                registerFragment = true,
                loginFragment = false,
            ).onEach { dataState ->

                dataState.data?.let {email->
                    this.state.value = state.copy(registration_email = email.email)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message.equals(ErrorHandling.ERROR_NO_EMAIL_SAVED)){
                        Log.d(TAG, ErrorHandling.ERROR_NO_EMAIL_SAVED)
                    } else {
                        Log.d(TAG, "${stateMessage.response.message}")
                    }
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
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

}