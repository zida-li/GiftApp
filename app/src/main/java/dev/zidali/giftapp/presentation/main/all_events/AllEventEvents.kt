package dev.zidali.giftapp.presentation.main.all_events

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class AllEventEvents {

    object FetchEvents: AllEventEvents()

    data class TurnOnNotifications(
        val contactEvent: ContactEvent,
        val reminderPickerResult: String,
    ): AllEventEvents()

    data class TurnOffNotifications(
        val contactEvent: ContactEvent,
    ): AllEventEvents()

    data class SetContactHolder(
        val contactEvent: ContactEvent,
        val reminder: String,
    ): AllEventEvents()

    data class SetToolBarState(
        val state: AllEventToolbarState
    ): AllEventEvents()

    data class AddOrRemoveContactEventFromSelectedList(
        val contactEvent: ContactEvent
    ): AllEventEvents()

    object ClearSelectedContactEvents: AllEventEvents()

    object DeleteSelectedContactEvents: AllEventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): AllEventEvents()

    object OnRemoveHeadFromQueue: AllEventEvents()

}