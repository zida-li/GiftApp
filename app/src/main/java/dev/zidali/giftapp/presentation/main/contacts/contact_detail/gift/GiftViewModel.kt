package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.CreateContact
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class GiftViewModel
@Inject
constructor(
    private val createContact: CreateContact,
    private val firebaseAuth: FirebaseAuth,
    private val appDataStore: AppDataStore,
): ViewModel() {

    val state: MutableLiveData<GiftState> = MutableLiveData(GiftState())

    fun onTriggerEvent(event: GiftEvents) {

        when(event) {
            is GiftEvents.FetchGifts -> {
                fetchGifts()
            }
            is GiftEvents.FetchContactName -> {
                fetchContactName()
            }
            is GiftEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is GiftEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchGifts() {
        state.value?.let { state->
            
        }
    }

    private fun fetchContactName() {
        state.value?.let {state->
            flow<GiftState> {
                val contactName = appDataStore.readValue(DataStoreKeys.SELECTED_CONTACT_NAME)
                emit(GiftState(
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