package dev.zidali.giftapp.presentation.main.create_contact

import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.presentation.auth.login.LoginEvents

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