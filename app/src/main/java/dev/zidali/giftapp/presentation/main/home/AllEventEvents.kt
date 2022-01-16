package dev.zidali.giftapp.presentation.main.home

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class AllEventEvents {

    object FetchEvents: AllEventEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): AllEventEvents()

    object OnRemoveHeadFromQueue: AllEventEvents()

}