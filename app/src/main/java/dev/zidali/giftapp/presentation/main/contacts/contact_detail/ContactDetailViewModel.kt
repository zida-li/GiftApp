package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel
@Inject
constructor(
    private val appDataStore: AppDataStore,
): ViewModel() {

    val state: MutableLiveData<ContactDetailState> = MutableLiveData(ContactDetailState())

    fun onTriggerEvent(event: ContactDetailEvents) {

        when(event) {
            is ContactDetailEvents.FetchContactName -> {
                fetchContactName()
            }
            is ContactDetailEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ContactDetailEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchContactName() {
        state.value?.let {state->
            flow<ContactDetailState> {
                val contactName = appDataStore.readValue(DataStoreKeys.SELECTED_CONTACT_NAME)
                emit(ContactDetailState(
                    contact_name = contactName!!
                ))
            }.onEach {
                this.state.value = state.copy(contact_name = it.contact_name)
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