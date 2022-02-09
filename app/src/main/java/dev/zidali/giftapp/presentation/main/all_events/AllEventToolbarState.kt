package dev.zidali.giftapp.presentation.main.all_events

sealed class AllEventToolbarState {

    object MultiSelectionState: AllEventToolbarState()

    object RegularState: AllEventToolbarState()

}