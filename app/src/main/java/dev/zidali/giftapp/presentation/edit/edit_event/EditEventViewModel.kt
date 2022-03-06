package dev.zidali.giftapp.presentation.edit.edit_event

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.CalendarSelection
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.shared.FetchEvent
import dev.zidali.giftapp.business.interactors.main.shared.UpdateContactEventReminder
import dev.zidali.giftapp.business.interactors.main.shared.UpdateEvent
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class EditEventViewModel
@Inject
constructor(
    private val fetchEvent: FetchEvent,
    private val updateContactEventReminder: UpdateContactEventReminder,
    private val updateEvent: UpdateEvent,
    private val savedStateHandle: SavedStateHandle,
    private val appDataStore: AppDataStore,
): ViewModel() {

    val state: MutableLiveData<EditEventState> = MutableLiveData(EditEventState())

    init {
        val contactName = savedStateHandle.get<Int>("CONTACT_PK")
        val contactEvent = savedStateHandle.get<Int>("EVENT_PK")
        onTriggerEvent(EditEventEvents.FetchEvent(contactName!!, contactEvent!!))
    }

    fun onTriggerEvent(event: EditEventEvents) {

        when(event) {
            is EditEventEvents.FetchEvent -> {
                fetchEvent(event.contact_pk, event.event_pk)
            }
            is EditEventEvents.UpdateContactEvent -> {
                updateContactEvent()
            }
            is EditEventEvents.OnUpdateEvent -> {
                onUpdateEvent(event.event)
            }
            is EditEventEvents.OnUpdateDatePicker -> {
                onUpdateDatePicker(
                    event.date,
                    event.month,
                    event.year,
                )
            }
            is EditEventEvents.OnUpdateReminderPicker -> {
                onUpdateReminderPicker(
                    event.reminder
                )
            }
            is EditEventEvents.OnUpdateYmdFormat -> {
                onUpdateYmdFormat(event.ymdFormat)
            }
            is EditEventEvents.TurnOnNotifications -> {
                turnOnNotifications(event.contactEvent, event.reminderPickerResult)
            }
            is EditEventEvents.TurnOffNotifications -> {
                turnOffNotifications(event.contactEvent)
            }
            is EditEventEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is EditEventEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchEvent(contact_pk: Int, event_pk: Int) {
        state.value?.let { state->
            fetchEvent.execute(
                contact_pk,
                event_pk
            ).onEach { dataState ->

                dataState.data?.let { event->
                    this.state.value = state.copy(
                        contact_event = event,
                        reminderSelectionHolder = event.contact_event_reminder,
                        calendarSelectionHolder = CalendarSelection(
                            selectedYear = event.year,
                            selectedMonth = event.month,
                            selectedDay = event.day,
                         ),
                        ymd_formatHolder = event.ymd_format,
                    )
                }

                setInitialLoadComplete(true)

            }.launchIn(viewModelScope)

        }
    }

    private fun onUpdateEvent(event: String) {
        state.value?.let { state->
            this.state.value = state.copy(
                event_holder = event
            )
        }
    }

    private fun onUpdateDatePicker(day: Int, month: Int, year: Int) {
        state.value?.let{ state->
            this.state.value = state.copy(
                calendarSelectionHolder = CalendarSelection(
                    selectedYear = year,
                    selectedMonth = month,
                    selectedDay = day,
                )
            )
        }
    }

    private fun onUpdateReminderPicker(reminder: String) {
        state.value?.let { state->
            this.state.value = state.copy(
                reminderSelectionHolder = reminder
            )
        }
    }

    private fun onUpdateYmdFormat(ymd_format: String) {
        state.value?.let { state->
            this.state.value = state.copy(
                ymd_formatHolder = ymd_format
            )
        }
    }

    private fun updateContactEvent() {
        cacheState()
        state.value?.let { state->
            val updateEventError = EditEventState(
                update_contact_event = state.update_contact_event,
            ).isValid()
            if(updateEventError == EditEventState.UpdateEventError.none()) {
                updateEvent.execute(
                    state.update_contact_event!!,
                ).onEach { dataState ->

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }

                    appDataStore.setValue(DataStoreKeys.EVENT_PK, state.contact_event?.event_pk.toString())
                    appDataStore.setValue(DataStoreKeys.CONTACT_PK, state.contact_event?.contact_pk.toString())

                    setUpdateEventSuccessful(true)

                }.launchIn(viewModelScope)
            } else {
                appendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = updateEventError,
                            uiComponentType = UIComponentType.Dialog,
                            messageType = MessageType.Error,
                        )
                    )
                )
            }
        }

    }

    private fun cacheState() {

        state.value?.let { state->

            this.state.value = state.copy(
                update_contact_event = ContactEvent(
                    contact_name = state.contact_event?.contact_name!!,
                    contact_event = state.event_holder,
                    contact_event_reminder = state.reminderSelectionHolder,
                    year = state.calendarSelectionHolder.selectedYear,
                    month = state.calendarSelectionHolder.selectedMonth,
                    day = state.calendarSelectionHolder.selectedDay,
                    contact_pk = state.contact_event.contact_pk,
                    ymd_format = state.ymd_formatHolder,
                    expired = state.contact_event.expired,
                    event_pk = state.contact_event.event_pk,
                )
            )
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

    private fun setUpdateEventSuccessful(addEventSuccessful: Boolean) {
        state.value?.let {state->
            this.state.value = state.copy(editEventSuccessful = addEventSuccessful)
        }
    }

    private fun setInitialLoadComplete(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(initialLoadComplete = boolean)
        }
    }

}