package dev.zidali.giftapp.presentation.main.fab.create_event

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class CreateEventEvents {

    object FetchContacts: CreateEventEvents()

    data class OnUpdateEvent(
        var event: String,
    ): CreateEventEvents()

    data class OnUpdateContactSelection(
        var contact: String,
    ): CreateEventEvents()

    data class OnUpdateDatePicker(
        var date: Int,
        var month: Int,
        var year: Int,
    ): CreateEventEvents()

    data class OnUpdateReminderPicker(
        var reminder: String,
    ): CreateEventEvents()

    object CreateEvent: CreateEventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): CreateEventEvents()

    object OnRemoveHeadFromQueue: CreateEventEvents()

}