package dev.zidali.giftapp.presentation.main.fab.create_event

import android.content.Context
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class CreateEventEvents {

    data class FetchContacts(
        val email: String,
        val context: Context,
    ): CreateEventEvents()

    object FetchCurrentContact: CreateEventEvents()

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

    data class OnUpdateYmdFormat(
        var ymdFormat: String,
    ): CreateEventEvents()

    object CreateEvent: CreateEventEvents()

    data class SetDataLoaded(
        var boolean: Boolean
    ): CreateEventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): CreateEventEvents()

    object OnRemoveHeadFromQueue: CreateEventEvents()

}