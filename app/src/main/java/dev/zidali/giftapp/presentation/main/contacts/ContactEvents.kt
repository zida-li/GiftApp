package dev.zidali.giftapp.presentation.main.contacts

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class ContactEvents {

    object FetchContacts: ContactEvents()

    data class PassDataToViewPager(
        val contact_name: String,
        val contact_pk: Int,
    ): ContactEvents()

    object ResetContactName: ContactEvents()

    data class SetFirstLoad(
        val boolean: Boolean
    ): ContactEvents()

    data class SetToolBarState(
        val state: ContactToolbarState
    ): ContactEvents()

    data class AddOrRemoveContactFromSelectedList(
        val contact: Contact
    ): ContactEvents()

    object ClearSelectedContacts: ContactEvents()

    object DeleteSelectedContacts: ContactEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): ContactEvents()

    object OnRemoveHeadFromQueue: ContactEvents()

}