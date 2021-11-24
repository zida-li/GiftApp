package dev.zidali.giftapp.presentation.main.create_contact

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.CreateContact
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreateContactViewModel
@Inject
constructor(
    private val createContact: CreateContact,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {

    val state: MutableLiveData<CreateContactState> = MutableLiveData(CreateContactState())

    fun onTriggerEvent(event: CreateContactEvents) {
        when(event) {
            is CreateContactEvents.OnUpdateName -> {
                onUpdateName(event.name)
            }
            is CreateContactEvents.CreateContact -> {
                createContact()
            }
            is CreateContactEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateContactEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun onUpdateName(name: String){
        state.value?.let { state->
            this.state.value = state.copy(
                name = name,
                contact = Contact(
                    name,
                    current_authUser_email = firebaseAuth.currentUser?.email
                )
            )

        }
    }

    private fun createContact() {
        state.value?.let {state->
            val createContactError = CreateContactState(
                state.name
            ).isValid()
            if(createContactError == CreateContactState.CreateContactError.none()) {
                createContact.execute(
                    state.contact
                ).onEach { dataState ->

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }

                }.launchIn(viewModelScope)
            } else {
                appendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = createContactError,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error
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
}