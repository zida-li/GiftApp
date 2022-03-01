package dev.zidali.giftapp.presentation.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import dev.zidali.giftapp.business.domain.util.SuccessHandling.Companion.SUCCESS_DELETE_ACCOUNT
import dev.zidali.giftapp.business.domain.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.session.CheckPreviousAuthUser
import dev.zidali.giftapp.business.interactors.session.DeleteAccount
import dev.zidali.giftapp.business.interactors.session.Logout
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    private val checkPreviousAuthUser: CheckPreviousAuthUser,
    private val logout: Logout,
    private val deleteAccount: DeleteAccount,
    private val appDataStoreManager: AppDataStore,
) {

    private val TAG: String = "AppDebug"

    private val sessionScope = CoroutineScope(Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        // Check if a user was authenticated in a previous session
        sessionScope.launch {
                onTriggerEvent(SessionEvents.CheckPreviousAuthUser)
            }
        }

    fun onTriggerEvent(event: SessionEvents){
        when(event){
            is SessionEvents.Login -> {
                login(event.accountProperties)
            }
            is SessionEvents.Logout -> {
                logout()
            }
            is SessionEvents.DeleteAccount -> {
                deleteAccount()
            }
            is SessionEvents.CheckPreviousAuthUser -> {
                checkPreviousAuthUser()
            }
            is SessionEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
            is SessionEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
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

    private fun checkPreviousAuthUser(){
        state.value?.let { state ->
            checkPreviousAuthUser.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { accountProperties ->
                    this.state.value = state.copy(accountProperties = accountProperties)
                    onTriggerEvent(SessionEvents.Login(accountProperties))
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                        onFinishCheckingPrevAuthUser()
                    }
                    else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(sessionScope)
        }
    }

    private fun login(accountProperties: AccountProperties){
        state.value?.let { state ->
            this.state.value = state.copy(accountProperties = accountProperties)
        }
    }

    private fun logout(){
        state.value?.let { state ->
            logout.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { response ->
                    if(response.message.equals(SUCCESS_LOGOUT)){
                        this.state.value = state.copy(accountProperties = null)
                        clearAuthUser()
                        onFinishCheckingPrevAuthUser()
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(sessionScope)
        }
    }

    private fun deleteAccount() {

        state.value?.let { state->
            deleteAccount.execute().onEach { dataState ->

                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { response ->
                    if(response.message.equals(SUCCESS_DELETE_ACCOUNT)){
                        logout()
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(sessionScope)
        }

    }

    private fun onFinishCheckingPrevAuthUser(){
        state.value?.let { state ->
            this.state.value = state.copy(didCheckForPreviousAuthUser = true)
        }
    }

    private fun clearAuthUser() {
        sessionScope.launch {
            appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, "")
        }
    }

}