package dev.zidali.giftapp.presentation.edit.event_detail

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.main.shared.FetchEvent
import dev.zidali.giftapp.business.interactors.main.shared.UpdateContactEventReminder
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventDetailViewModel
@Inject
constructor(
    private val fetchEvent: FetchEvent,
    private val updateContactEventReminder: UpdateContactEventReminder,
    private val savedStateHandle: SavedStateHandle,
    private val appDataStore: AppDataStore,
): ViewModel() {

    val state: MutableLiveData<EventDetailState> = MutableLiveData(EventDetailState())

    fun onTriggerEvent(event: EventDetailEvents) {

        when(event) {
            is EventDetailEvents.FetchEvent -> {
                fetchEvent(event.contact_pk, event.event_pk)
            }
            is EventDetailEvents.Refresh -> {
                refresh()
            }
            is EventDetailEvents.TurnOnNotifications -> {
                turnOnNotifications(event.contactEvent, event.reminderPickerResult)
            }
            is EventDetailEvents.TurnOffNotifications -> {
                turnOffNotifications(event.contactEvent)
            }
            is EventDetailEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is EventDetailEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchEvent(contact_pk: Int, event_pk: Int,) {
        state.value?.let { state->
            fetchEvent.execute(
                contact_pk,
                event_pk,
            ).onEach { dataState ->

                dataState.data?.let { event->
                    this.state.value = state.copy(contact_event = event)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun refresh() {

        state.value?.let { state->

            flow<EventDetailState> {
                val eventPk = appDataStore.readValue(DataStoreKeys.EVENT_PK)
                val contactPk = appDataStore.readValue(DataStoreKeys.CONTACT_PK)
                emit(EventDetailState(
                    event_pk = eventPk!!,
                    contact_pk = contactPk!!,
                ))
            }.onEach {
                this.state.value = state.copy(
                    event_pk = it.event_pk,
                    contact_pk = it.contact_pk,
                )
            }.launchIn(viewModelScope)

        }

        reload()
    }

    private fun reload() {
        state.value?.let { state->
            fetchEvent.execute(
                state.contact_pk.toInt(),
                state.event_pk.toInt(),
            ).onEach { dataState ->
                dataState.data?.let { event->
                    this.state.value = state.copy(contact_event = event)
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun turnOnNotifications(contactEvent: ContactEvent, reminderPickerResult: String) {

        state.value?.let { state->

            contactEvent.contact_event_reminder = reminderPickerResult

            updateContactEventReminder.execute(contactEvent).onEach { dataState ->

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
        }
    }


    private fun turnOffNotifications(contactEvent: ContactEvent) {

        state.value?.let { state->

            contactEvent.contact_event_reminder = ""

            updateContactEventReminder.execute(contactEvent).onEach { dataState ->

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }

            }.launchIn(viewModelScope)
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

}