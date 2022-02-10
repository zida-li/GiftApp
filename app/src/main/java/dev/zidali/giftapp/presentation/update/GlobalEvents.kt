package dev.zidali.giftapp.presentation.update

sealed class GlobalEvents {

    data class SetNeedToUpdate(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetNeedToUpdateEventFragment(
        val boolean: Boolean
    ): GlobalEvents()

    data class GiftFragmentInView(
        val boolean: Boolean
    ): GlobalEvents()

    data class EventFragmentInView(
        val boolean: Boolean
    ): GlobalEvents()

    data class EditFragmentInView(
        val boolean: Boolean
    ): GlobalEvents()

}
