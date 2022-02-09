package dev.zidali.giftapp.presentation.main.shared.event_detail

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class EventDetailEvents {

    data class FetchEvent(
        val contact_name: String,
        val contact_event: String
    ): EventDetailEvents()

    object Refresh: EventDetailEvents()

    data class TurnOnNotifications(
        val contactEvent: ContactEvent,
        val reminderPickerResult: String,
    ): EventDetailEvents()

    data class TurnOffNotifications(
        val contactEvent: ContactEvent,
    ): EventDetailEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): EventDetailEvents()

    object OnRemoveHeadFromQueue: EventDetailEvents()

}