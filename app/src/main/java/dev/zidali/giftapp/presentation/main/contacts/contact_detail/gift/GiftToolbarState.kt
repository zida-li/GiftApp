package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

sealed class GiftToolbarState {

    object MultiSelectionState: GiftToolbarState()

    object RegularState: GiftToolbarState()

}