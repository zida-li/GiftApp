package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchEvents
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventViewModel
@Inject
constructor(
    private val appDataStore: AppDataStore,
    private val fetchEvents: FetchEvents
): ViewModel() {

    val state: MutableLiveData<EventState> = MutableLiveData(EventState())

    fun onTriggerEvent(event: EventEvents) {

        when(event) {
            is EventEvents.FetchEvents -> {
                fetchEvents()
            }
            is EventEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is EventEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchEvents() {
        state.value?.let { state->
            fetchEvents.execute(
                state.contact_name
            ).onEach { dataState ->

                dataState.data?.let { event->
                    this.state.value = state.copy(contact_events = event.contact_events)
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