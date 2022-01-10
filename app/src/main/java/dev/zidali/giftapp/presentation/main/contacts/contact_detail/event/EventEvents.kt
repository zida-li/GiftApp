package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class EventEvents {

    object FetchEvents: EventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): EventEvents()

    object OnRemoveHeadFromQueue: EventEvents()

}