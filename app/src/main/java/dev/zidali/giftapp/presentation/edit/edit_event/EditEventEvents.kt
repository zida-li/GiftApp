package dev.zidali.giftapp.presentation.edit.edit_event

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventEvents

sealed class EditEventEvents {

    data class FetchEvent(
        val contact_pk: Int,
        val event_pk: Int,
    ): EditEventEvents()

    object UpdateContactEvent: EditEventEvents()

    data class OnUpdateEvent(
        var event: String,
    ): EditEventEvents()

    data class OnUpdateDatePicker(
        var date: Int,
        var month: Int,
        var year: Int,
    ): EditEventEvents()

    data class OnUpdateReminderPicker(
        var reminder: String,
    ): EditEventEvents()

    data class OnUpdateYmdFormat(
        var ymdFormat: String,
    ): EditEventEvents()

    data class TurnOnNotifications(
        val contactEvent: ContactEvent,
        val reminderPickerResult: String,
    ): EditEventEvents()

    data class TurnOffNotifications(
        val contactEvent: ContactEvent,
    ): EditEventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): EditEventEvents()

    object OnRemoveHeadFromQueue: EditEventEvents()

}