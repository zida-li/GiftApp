package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class GiftEvents {

    object FetchContactName: GiftEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): GiftEvents()

    object OnRemoveHeadFromQueue: GiftEvents()

}