package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

sealed class EventToolbarState {

    object MultiSelectionState: EventToolbarState()

    object RegularState: EventToolbarState()

}