package dev.zidali.giftapp.presentation.main.fab.add_gift

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.fab.AddGift
import dev.zidali.giftapp.business.interactors.main.shared.FetchContacts
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AddGiftViewModel
@Inject
constructor(
    private val addGift: AddGift,
    private val fetchContacts: FetchContacts,
): ViewModel() {

    val state: MutableLiveData<AddGiftState> = MutableLiveData(AddGiftState())

    fun onTriggerEvent(event: AddGiftEvents) {
        when(event) {
            is AddGiftEvents.FetchContacts ->
                fetchContacts()
            is AddGiftEvents.OnUpdateGift -> {
                onUpdateGift(event.contact, event.gift)
            }
            is AddGiftEvents.AddGift -> {
                addGift()
            }
            is AddGiftEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is AddGiftEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchContacts(){
        state.value?.let {state->
            fetchContacts.execute().onEach {dataState ->

                val contactNames: MutableList<String> = mutableListOf()

                dataState.data?.let { contacts->
                    for (contact in contacts) {
                        contactNames.add(contact.contact_name!!)
                    }
                    this.state.value = state.copy(contacts = contactNames)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun onUpdateGift(contact: String, gift: String) {
        state.value?.let { state->
            this.state.value = state.copy(
                contact_name_holder = contact,
                contact_gift_holder = gift,
            )
        }
    }

    private fun addGift() {
        setAddGiftState()
        state.value?.let {state->
            val addGiftError = AddGiftState(
                gift = state.gift
            ).isValid()
            if(addGiftError == AddGiftState.CreateGiftError.none()) {
                addGift.execute(
                    state.gift
                ).onEach { dataState ->

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }

                    setAddGiftSuccessful(true)

                }.launchIn(viewModelScope)
            } else {
                appendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = addGiftError,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error,
                        )
                    )
                )
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

    private fun setAddGiftSuccessful(addGiftSuccessful: Boolean) {
        state.value?.let {state->
            this.state.value = state.copy(addGiftSuccessful = addGiftSuccessful)
        }
    }

    private fun setAddGiftState(){
        state.value?.let { state->
            this.state.value = state.copy(
                gift = Gift(
                    contact_gift = state.contact_gift_holder,
                    contact_name = state.contact_name_holder,
                    pk = 0,
                )
            )
        }
    }

}