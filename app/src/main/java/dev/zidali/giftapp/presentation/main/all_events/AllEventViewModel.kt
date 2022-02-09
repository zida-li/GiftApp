package dev.zidali.giftapp.presentation.main.all_events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.business.domain.util.doesMessageAlreadyExistInQueue
import dev.zidali.giftapp.business.interactors.main.shared.DeleteEvents
import dev.zidali.giftapp.business.interactors.main.shared.FetchAllEvents
import dev.zidali.giftapp.business.interactors.main.shared.UpdateContactEventReminder
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AllEventViewModel
@Inject
constructor(
    private val fetchAllEvents: FetchAllEvents,
    private val deleteEvents: DeleteEvents,
    private val updateContactEventReminder: UpdateContactEventReminder,
): ViewModel() {

    val state: MutableLiveData<AllEventState> = MutableLiveData(AllEventState())

    val allEventListInteractionManager = AllEventListInteractionManager()

    val toolbarState: LiveData<AllEventToolbarState>
        get() = allEventListInteractionManager.toolbarState

    fun onTriggerEvent(event: AllEventEvents) {

        when(event) {
            is AllEventEvents.FetchEvents -> {
                fetchEvents()
            }
            is AllEventEvents.TurnOnNotifications -> {
                turnOnNotifications(event.contactEvent, event.reminderPickerResult)
            }
            is AllEventEvents.TurnOffNotifications -> {
                turnOffNotifications(event.contactEvent)
            }
            is AllEventEvents.SetContactHolder -> {
                setContactHolder(event.contactEvent, event.reminder)
            }
            is AllEventEvents.SetToolBarState -> {
                setToolBarState(event.state)
            }
            is AllEventEvents.ClearSelectedContactEvents -> {
                clearSelectedContactEvents()
            }
            is AllEventEvents.DeleteSelectedContactEvents -> {
                deleteSelectedContactEvents()
            }
            is AllEventEvents.AddOrRemoveContactEventFromSelectedList -> {
                addOrRemoveContactEventFromSelectedList(event.contactEvent)
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

    private fun turnOnNotifications(contactEvent: ContactEvent, reminderPickerResult: String) {
        state.value?.let { state->

            for(event in state.contact_events) {
                if(event.contact_event == contactEvent.contact_event
                    && event.contact_name == contactEvent.contact_name) {

                    contactEvent.contact_event_reminder = reminderPickerResult

                    updateContactEventReminder.execute(contactEvent).onEach { dataState ->

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                        }

                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    private fun turnOffNotifications(contactEvent: ContactEvent) {
        state.value?.let { state->

            for(event in state.contact_events) {
                if(event.contact_event == contactEvent.contact_event
                    && event.contact_name == contactEvent.contact_name) {

                    contactEvent.contact_event_reminder = ""

                    updateContactEventReminder.execute(contactEvent).onEach { dataState ->

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                        }

                    }.launchIn(viewModelScope)
                }
            }
        }
    }

    private fun setContactHolder(contactEvent: ContactEvent, reminder: String) {
        this.state.value?.let { state->
            this.state.value = state.copy(
                contact_event_holder = ContactEvent(
                    contact_name = contactEvent.contact_name,
                    contact_event = contactEvent.contact_event,
                    contact_event_reminder = reminder,
                    year = contactEvent.year,
                    month = contactEvent.month,
                    day = contactEvent.day,
                    pk = contactEvent.pk,
                )
            )
        }
    }

    /**
     * MultiSelectionMode
     */

    private fun setToolBarState(state: AllEventToolbarState) {
        allEventListInteractionManager.setToolBarState(state)
    }

    private fun addOrRemoveContactEventFromSelectedList(contactEvents: ContactEvent) {
        allEventListInteractionManager.addOrRemoveEventFromSelectedList(contactEvents)
    }

    private fun clearSelectedContactEvents() {
        allEventListInteractionManager.clearSelectedEvents()
    }

    private fun deleteSelectedContactEvents() {
        if(getSelectedContactEvents().size > 0) {
            deleteEvents.execute(getSelectedContactEvents()).launchIn(viewModelScope)
            removeSelectedContactEventsFromList()
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

    private fun getSelectedContactEvents(): ArrayList<ContactEvent> {
        return allEventListInteractionManager.getSelectedEvents()
    }

    private fun removeSelectedContactEventsFromList() {
        state.value?.contact_events?.removeAll(getSelectedContactEvents())
        clearSelectedContactEvents()
    }

}