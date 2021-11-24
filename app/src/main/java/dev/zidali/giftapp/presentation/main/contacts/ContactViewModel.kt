package dev.zidali.giftapp.presentation.main.contacts

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.main.contacts.FetchContacts
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
@Inject
constructor(
    private val fetchContacts: FetchContacts,
): ViewModel() {

    val state: MutableLiveData<ContactState> = MutableLiveData(ContactState())

    fun onTriggerEvent(event: ContactEvents){
        when (event) {
            is ContactEvents.FetchContacts -> {
                fetchContacts()
            }
            is ContactEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ContactEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchContacts() {

        state.value?.let { state->
            fetchContacts.execute().onEach {dataState ->

                dataState.data?.let { contactList->
                    this.state.value = state.copy(contactList = contactList)
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