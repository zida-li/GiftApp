package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class EventEvents {

    object FetchContactName: EventEvents()

    object FetchEvents: EventEvents()

    data class SetFirstLoad(
        var boolean: Boolean
    ): EventEvents()

    data class SetToolBarState(
        val state: EventToolbarState
    ): EventEvents()

    data class AddOrRemoveContactEventFromSelectedList(
        val contactEvent: ContactEvent
    ): EventEvents()

    object ClearSelectedContactEvents: EventEvents()

    object DeleteSelectedContactEvents: EventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): EventEvents()

    object OnRemoveHeadFromQueue: EventEvents()

}