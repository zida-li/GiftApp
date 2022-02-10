package dev.zidali.giftapp.presentation.main.shared.edit_event

import dev.zidali.giftapp.business.domain.models.CalendarSelection
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class EditEventState(
    val contact_event: ContactEvent? = null,
    val event_holder: String = "",
    val calendarSelectionHolder: CalendarSelection = CalendarSelection(
        selectedYear = 0,
        selectedMonth = 0,
        selectedDay = 0,
    ),
    val reminderSelectionHolder: String = "",
    val initial_contact_event_holder: ContactEvent? = null,
    val update_contact_event: ContactEvent? = null,
    val initialLoadComplete: Boolean = false,
    val ymd_formatHolder: String = "",
    val isLoading: Boolean = false,
    var editEventSuccessful: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class UpdateEventError {
        companion object{

            fun mustFillAllFields(): String{
                return "Event cannot be blank"
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValid(): String{
        if(update_contact_event?.contact_event == "") {
            return EditEventState.UpdateEventError.mustFillAllFields()
        }
        return EditEventState.UpdateEventError.none()
    }

}