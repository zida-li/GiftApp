package dev.zidali.giftapp.presentation.main.contacts

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class ContactEvents {

    object FetchContacts: ContactEvents()

    data class PassDataToViewPager(
        val contact_name: String,
    ): ContactEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): ContactEvents()

    object OnRemoveHeadFromQueue: ContactEvents()

}