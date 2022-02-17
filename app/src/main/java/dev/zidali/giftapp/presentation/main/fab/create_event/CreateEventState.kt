package dev.zidali.giftapp.presentation.main.fab.create_event

import dev.zidali.giftapp.business.domain.models.CalendarSelection
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class CreateEventState(
    var contacts: MutableList<String> = mutableListOf(),
    var event: String = "",
    var createEvent: ContactEvent = ContactEvent(
        contact_name = "",
        contact_event = "",
        contact_event_reminder = "",
        year = 0,
        month = 0,
        day = 0,
        pk = 0,
        ymd_format = "",
        expired = false,
    ),
    var calendarSelectionHolder: CalendarSelection = CalendarSelection(
        selectedYear = 0,
        selectedMonth = 0,
        selectedDay = 0,
    ),
    var ymd_formatHolder: String = "",
    var reminderSelectionHolder: String = "",
    var selectedContact: String = "",
    var current_contact_name: String = "",
    var addEventSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    var dataLoaded: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class CreateEventError {
        companion object{

            fun mustFillAllFields(): String{
                return "Event cannot be blank"
            }

            fun mustSelectContact(): String {
                return "You must select a contact"
            }

            fun mustSelectDate(): String {
                return "You must select a date"
            }

            fun mustSelectReminder(): String {
                return "You must select reminder intervals, this can be altered later"
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValid(): String{
        if(createEvent.contact_event == "") {
            return CreateEventError.mustFillAllFields()
        }
        if (createEvent.contact_name == "") {
            return CreateEventError.mustSelectContact()
        }
        if(createEvent.day == 0) {
            return CreateEventError.mustSelectDate()
        }
        if(createEvent.contact_event_reminder == "") {
            return CreateEventError.mustSelectReminder()
        }

        return CreateEventError.none()
    }
}