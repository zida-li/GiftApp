package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class ContactDetailEvents {

    object FetchContactName: ContactDetailEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): ContactDetailEvents()

    object OnRemoveHeadFromQueue: ContactDetailEvents()

}