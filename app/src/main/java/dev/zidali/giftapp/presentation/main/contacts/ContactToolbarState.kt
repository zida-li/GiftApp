package dev.zidali.giftapp.presentation.main.contacts

sealed class ContactToolbarState {

    object MultiSelectionState: ContactToolbarState()

    object RegularState: ContactToolbarState()

}