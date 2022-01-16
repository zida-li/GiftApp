package dev.zidali.giftapp.presentation.main.home

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.main.shared.FetchAllEvents
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AllEventViewModel
@Inject
constructor(
    private val fetchAllEvents: FetchAllEvents,
): ViewModel() {

    val state: MutableLiveData<AllEventState> = MutableLiveData(AllEventState())

    fun onTriggerEvent(event: AllEventEvents) {

        when(event) {
            is AllEventEvents.FetchEvents -> {
                fetchEvents()
            }
            is AllEventEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is AllEventEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchEvents() {
        state.value?.let { state->
            fetchAllEvents.execute().onEach { dataState ->

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