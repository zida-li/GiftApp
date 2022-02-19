package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.shared.UpdateContact
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
    private val updateContact: UpdateContact,
    private val savedStateHandle: SavedStateHandle,
): ViewModel() {

    val state: MutableLiveData<ContactDetailState> = MutableLiveData(ContactDetailState())

    init {
        state.value?.let { state->
            savedStateHandle.get<String>("selectedContact")?.let { contact_name->
                this.state.value = state.copy(contact_name = contact_name)
            }
        }
        state.value?.let { state->
            savedStateHandle.get<Int>("selectedContactPk")?.let { contact_pk->
                this.state.value = state.copy(contact_pk = contact_pk)
            }
        }
    }

    fun onTriggerEvent(event: ContactDetailEvents) {

        when(event) {
            is ContactDetailEvents.OnUpdateContact -> {
                onUpdateContact(event.new_name)
            }
            is ContactDetailEvents.UpdateContact -> {
                updateContact()
            }
            is ContactDetailEvents.UpdateTitle -> {
                updateTitle()
            }
            is ContactDetailEvents.ActivateEditMode -> {
                activateEditMode()
            }
            is ContactDetailEvents.DeactivateEditMode -> {
                deactivateEditMode()
            }
            is ContactDetailEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is ContactDetailEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun onUpdateContact(new_name: String) {
        state.value?.let {state->
            this.state.value = state.copy(
                changed_name = new_name
            )
        }
    }

    private fun updateTitle(){
        state.value?.let { state->
            this.state.value = state.copy(
                contact_name = state.changed_name
            )
        }
    }

    private fun updateContact() {

        state.value?.let { state->

            updateContact.execute(
                state.contact_pk,
                state.changed_name,
            ).onEach { dataState ->

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)

        }
    }

    private fun activateEditMode() {
        state.value?.let { state->
            this.state.value = state.copy(
                isEditing = true
            )
        }
    }

    private fun deactivateEditMode() {
        state.value?.let { state->
            this.state.value = state.copy(
                isEditing = false
            )
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