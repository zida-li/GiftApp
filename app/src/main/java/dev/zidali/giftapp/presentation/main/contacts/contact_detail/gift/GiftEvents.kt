package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class GiftEvents {

    object FetchContactPk: GiftEvents()

    object FetchGifts: GiftEvents()

    data class SetFirstLoad(
        var boolean: Boolean,
    ): GiftEvents()

    data class SetToolBarState(
        val state: GiftToolbarState
    ): GiftEvents()

    data class AddOrRemoveGiftFromSelectedList(
        val gift: Gift
    ): GiftEvents()

    object ClearSelectedGifts: GiftEvents()

    object DeleteSelectedGifts: GiftEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): GiftEvents()

    object OnRemoveHeadFromQueue: GiftEvents()

    data class SetMultiSelectionMode(
        val boolean: Boolean
    ): GiftEvents()

    data class SetIsCheckedGift(
        val gift: Gift,
        val position: Int
    ): GiftEvents()

}