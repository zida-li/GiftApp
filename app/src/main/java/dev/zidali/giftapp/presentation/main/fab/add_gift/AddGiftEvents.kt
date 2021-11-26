package dev.zidali.giftapp.presentation.main.fab.add_gift

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class AddGiftEvents {

    object FetchContacts: AddGiftEvents()

    data class OnUpdateGift(
        var contact: String,
        var gift: String,
    ): AddGiftEvents()

    object AddGift: AddGiftEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): AddGiftEvents()

    object OnRemoveHeadFromQueue: AddGiftEvents()

}