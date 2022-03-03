package dev.zidali.giftapp.presentation.main.fab.create_event

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.CalendarSelection
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.interactors.main.fab.CreateEvent
import dev.zidali.giftapp.business.interactors.main.shared.FetchContacts
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class CreateEventViewModel
@Inject
constructor(
    private val fetchContacts: FetchContacts,
    private val createEvent: CreateEvent,
    private val appDataStore: AppDataStore,
): ViewModel() {

    val state: MutableLiveData<CreateEventState> = MutableLiveData(CreateEventState())

    init {
        onUpdateReminderPicker("None")
    }

    fun onTriggerEvent(event: CreateEventEvents) {
        when(event) {
            is CreateEventEvents.FetchContacts ->
                fetchContacts(event.email)
            is CreateEventEvents.FetchCurrentContact -> {
                fetchCurrentContact()
            }
            is CreateEventEvents.OnUpdateEvent -> {
                onUpdateEvent(event.event)
            }
            is CreateEventEvents.OnUpdateContactSelection -> {
                onUpdateContactSelection(event.contact)
            }
            is CreateEventEvents.OnUpdateDatePicker -> {
                onUpdateDatePicker(event.date, event.month, event.year)
            }
            is CreateEventEvents.OnUpdateReminderPicker -> {
                onUpdateReminderPicker(event.reminder)
            }
            is CreateEventEvents.OnUpdateYmdFormat -> {
                onUpdateYmdFormat(event.ymdFormat)
            }
            is CreateEventEvents.CreateEvent -> {
                createEvent()
            }
            is CreateEventEvents.SetDataLoaded -> {
                setDataLoaded(event.boolean)
            }
            is CreateEventEvents.AppendToMessageQueue -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CreateEventEvents.OnRemoveHeadFromQueue -> {
                onRemoveHeadFromQueue()
            }
        }
    }

    private fun fetchContacts(email: String){
        state.value?.let {state->
            fetchContacts.execute(
                email
            ).onEach {dataState ->

                val contactNames: MutableList<String> = mutableListOf()

                dataState.data?.let { contacts->
                    for (contact in contacts) {
                        contactNames.add(contact.contact_name!!)
                    }
                    this.state.value = state.copy(
                        contact_display_list = contactNames,
                        contacts = contacts
                    )
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun fetchCurrentContact() {
        state.value?.let {state->
            flow<CreateEventState> {
                val contactName = appDataStore.readValue(DataStoreKeys.SELECTED_CONTACT_NAME)
                emit(CreateEventState(
                    current_contact_name = contactName!!
                ))
            }.onEach {
                this.state.value = state.copy(
                    current_contact_name = it.current_contact_name,
                )
            }.launchIn(viewModelScope)
        }
    }

    private fun onUpdateEvent(event: String) {
        state.value?.let { state->
            this.state.value = state.copy(
                event = event
            )
        }
    }

    private fun onUpdateContactSelection(contact: String) {

        state.value?.let { state->

            for(individualContact in state.contacts) {
                if(contact == individualContact.contact_name) {
                    this.state.value = state.copy(
                        selectedContact = contact,
                        contact_pk_holder = individualContact.contact_pk!!
                    )
                }
            }
        }
    }

    private fun onUpdateDatePicker(year: Int, month: Int, date: Int) {
        state.value?.let{ state->
            this.state.value = state.copy(
                calendarSelectionHolder = CalendarSelection(
                    selectedYear = year,
                    selectedMonth = month,
                    selectedDay = date,
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

    private fun createEvent() {
        setCreateEventState()
//        Log.d(Constants.TAG, state.value?.createEvent.toString())
        state.value?.let {state->
            val createEventError = CreateEventState(
                createEvent = state.createEvent,
            ).isValid()
            if(createEventError == CreateEventState.CreateEventError.none()) {
                createEvent.execute(state.createEvent).onEach { dataState ->

                    dataState.stateMessage?.let { stateMessage ->
                        appendToMessageQueue(stateMessage)
                    }

                    dataState.data?.let {
                        this.state.value = state.copy(
                            new_event_pk_holder = it.new_event_pk_holder
                        )
                    }

                    setPostCreateEventState()

                    setEventSuccessful(true)

                }.launchIn(viewModelScope)
            } else {
                appendToMessageQueue(
                    stateMessage = StateMessage(
                        response = Response(
                            message = createEventError,
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

    private fun setEventSuccessful(addEventSuccessful: Boolean) {
        state.value?.let {state->
            this.state.value = state.copy(addEventSuccessful = addEventSuccessful)
        }
    }

    private fun setCreateEventState(){
        state.value?.let { state->
            this.state.value = state.copy(
                createEvent = ContactEvent(
                    contact_name = state.selectedContact,
                    contact_event = state.event,
                    contact_event_reminder = state.reminderSelectionHolder,
                    year = state.calendarSelectionHolder.selectedYear,
                    month = state.calendarSelectionHolder.selectedMonth,
                    day = state.calendarSelectionHolder.selectedDay,
                    pk = state.contact_pk_holder,
                    ymd_format = state.ymd_formatHolder,
                    expired = false,
                    event_pk = 0,
                )
            )
        }
    }

    private fun setPostCreateEventState(){
        state.value?.let { state->
            this.state.value = state.copy(
                createEvent = ContactEvent(
                    contact_name = state.selectedContact,
                    contact_event = state.event,
                    contact_event_reminder = state.reminderSelectionHolder,
                    year = state.calendarSelectionHolder.selectedYear,
                    month = state.calendarSelectionHolder.selectedMonth,
                    day = state.calendarSelectionHolder.selectedDay,
                    pk = state.contact_pk_holder,
                    ymd_format = state.ymd_formatHolder,
                    expired = false,
                    event_pk = state.new_event_pk_holder,
                )
            )
        }
    }

    private fun setDataLoaded(boolean: Boolean) {
        state.value?.let { state->
            this.state.value = state.copy(
                dataLoaded = boolean
            )
        }
    }

}