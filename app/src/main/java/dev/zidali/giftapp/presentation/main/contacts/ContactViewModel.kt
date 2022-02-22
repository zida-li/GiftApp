package dev.zidali.giftapp.presentation.main.contacts

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.main.contacts.DeleteContacts
import dev.zidali.giftapp.business.interactors.main.shared.FetchContacts
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactViewModel
@Inject
constructor(
    private val fetchContacts: FetchContacts,
    private val appDataStore: AppDataStore,
    private val deleteContacts: DeleteContacts,
): ViewModel() {

    val state: MutableLiveData<ContactState> = MutableLiveData(ContactState())

    val contactListInteractionManager = ContactListInteractionManager()

    val toolbarState: LiveData<ContactToolbarState>
        get() = contactListInteractionManager.toolbarState

    fun onTriggerEvent(event: ContactEvents){
        when (event) {
            is ContactEvents.FetchContacts -> {
                fetchContacts()
            }
            is ContactEvents.PassDataToViewPager -> {
                passDataToViewPager(event.contact_name, event.contact_pk)
            }
            is ContactEvents.ResetContactName -> {
                resetContactName()
            }
            is ContactEvents.SetFirstLoad -> {
                setFirstLoad(event.boolean)
            }
            is ContactEvents.SetToolBarState -> {
                setToolBarState(event.state)
            }
            is ContactEvents.ClearSelectedContacts -> {
                clearSelectedContacts()
            }
            is ContactEvents.DeleteSelectedContacts -> {
                deleteSelectedContacts()
            }
            is ContactEvents.AddOrRemoveContactFromSelectedList -> {
                addOrRemoveContactFromSelectedList(event.contact)
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

                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { contactList->
                    this.state.value = state.copy(contactList = contactList)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun passDataToViewPager(contact_name: String, contact_pk: Int) {
        viewModelScope.launch {
            appDataStore.setValue(DataStoreKeys.SELECTED_CONTACT_PK, contact_pk.toString())
        }
        viewModelScope.launch {
            appDataStore.setValue(DataStoreKeys.SELECTED_CONTACT_NAME, contact_name)
        }
    }

    private fun resetContactName() {
        viewModelScope.launch {
            appDataStore.setValue(DataStoreKeys.SELECTED_CONTACT_NAME, "")
        }
    }

    private fun setFirstLoad(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(
                firstLoad = boolean
            )
        }
    }

    /**
     * MultiSelectionMode
     */

    private fun setToolBarState(state: ContactToolbarState) {
        contactListInteractionManager.setToolBarState(state)
    }

    private fun addOrRemoveContactFromSelectedList(contact: Contact) {
        contactListInteractionManager.addOrRemoveContactFromSelectedList(contact)
    }

    private fun clearSelectedContacts() {
        contactListInteractionManager.clearSelectedContacts()
    }

    private fun deleteSelectedContacts() {
        if(getSelectedContacts().size > 0) {
            deleteContacts.execute(getSelectedContacts()).launchIn(viewModelScope)
            removeSelectedContactsFromList()
        }
    }

    /**
     * Alert Dialogs
     */

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

    /**
     * Supporting Functions
     */

    private fun getSelectedContacts(): ArrayList<Contact> {
        return contactListInteractionManager.getSelectedContacts()
    }

    private fun removeSelectedContactsFromList() {
        state.value?.contactList?.removeAll(getSelectedContacts())
        clearSelectedContacts()
    }

}