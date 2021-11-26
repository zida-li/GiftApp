package dev.zidali.giftapp.presentation.main.fab.create_contact

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class CreateContactEvents {

    data class OnUpdateName(
        var name: String,
    ): CreateContactEvents()

    object CreateContact: CreateContactEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): CreateContactEvents()

    object OnRemoveHeadFromQueue: CreateContactEvents()

}