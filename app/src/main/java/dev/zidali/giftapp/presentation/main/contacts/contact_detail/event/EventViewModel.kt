package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.contacts.contact_detail.FetchEvents
import dev.zidali.giftapp.business.interactors.main.shared.DeleteEvents
import dev.zidali.giftapp.business.interactors.main.shared.UpdateContactEventReminder
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EventViewModel
@Inject
constructor(
    private val appDataStore: AppDataStore,
    private val fetchEvents: FetchEvents,
    private val deleteEvents: DeleteEvents,
    private val updateContactEventReminder: UpdateContactEventReminder,
): ViewModel() {

    val state: MutableLiveData<EventState> = MutableLiveData(EventState())

    val eventListInteractionManager = EventListInteractionManager()

    val toolbarState: LiveData<EventToolbarState>
        get() = eventListInteractionManager.toolbarState

    fun onTriggerEvent(event: EventEvents) {

        when(event) {
            is EventEvents.FetchContactPk -> {
                fetchContactPk()
            }
            is EventEvents.FetchEvents -> {
                fetchEvents()
            }
            is EventEvents.SetFirstLoad -> {
                setFirstLoad(event.boolean)
            }
            is EventEvents.TurnOnNotifications -> {
                turnOnNotifications(event.contactEvent, event.reminderPickerResult)
            }
            is EventEvents.TurnOffNotifications -> {
                turnOffNotifications(event.contactEvent)
            }
            is EventEvents.SetContactHolder -> {
                setContactHolder(event.contactEvent, event.reminder)
            }
            is EventEvents.SetToolBarState -> {
                setToolBarState(event.state)
            }
            is EventEvents.ClearSelectedContactEvents -> {
                clearSelectedContactEvents()
            }
            is EventEvents.DeleteSelectedContactEvents -> {
                deleteSelectedContactEvents()
            }
            is EventEvents.AddOrRemoveContactEventFromSelectedList -> {
                addOrRemoveContactEventFromSelectedList(event.contactEvent)
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
                state.contact_pk.toInt()
            ).onEach { dataState ->

                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { event->
                    this.state.value = state.copy(contact_events = event.contact_events)
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun fetchContactPk() {
        state.value?.let {state->
            flow<EventState> {
                val contactPk = appDataStore.readValue(DataStoreKeys.SELECTED_CONTACT_PK)
                emit(EventState(
                    contact_pk = contactPk!!
                ))
            }.onEach {
                this.state.value = state.copy(contact_pk = it.contact_pk)
            }.launchIn(viewModelScope)
        }
    }

    private fun setFirstLoad(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(
                firstLoad = boolean
            )
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

                    contactEvent.contact_event_reminder = "None"

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
                    contact_pk = contactEvent.contact_pk,
                    ymd_format = contactEvent.ymd_format,
                    expired = contactEvent.expired,
                    event_pk = contactEvent.event_pk,
                    event_owner = contactEvent.event_owner,
                )
            )
        }
    }

    /**
     * MultiSelectionMode
     */

    private fun setToolBarState(state: EventToolbarState) {
        eventListInteractionManager.setToolBarState(state)
    }

    private fun addOrRemoveContactEventFromSelectedList(contactEvents: ContactEvent) {
        eventListInteractionManager.addOrRemoveContactEventFromSelectedList(contactEvents)
    }

    private fun clearSelectedContactEvents() {
        eventListInteractionManager.clearSelectedContactEvents()
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
        return eventListInteractionManager.getSelectedContactEvents()
    }

    private fun removeSelectedContactEventsFromList() {
        state.value?.contact_events?.removeAll(getSelectedContactEvents())
        clearSelectedContactEvents()
    }
}