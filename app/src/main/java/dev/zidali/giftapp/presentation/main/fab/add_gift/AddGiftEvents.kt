package dev.zidali.giftapp.presentation.main.fab.add_gift

import android.content.Context
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class AddGiftEvents {

    data class FetchContacts(
        val email: String,
        val context: Context,
    ): AddGiftEvents()

    object FetchCurrentContact: AddGiftEvents()

    data class OnUpdateGift(
        var contact: String,
        var gift: String,
    ): AddGiftEvents()

    object AddGift: AddGiftEvents()

    data class SetDataLoaded(
        var boolean: Boolean
    ): AddGiftEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): AddGiftEvents()

    object OnRemoveHeadFromQueue: AddGiftEvents()

}