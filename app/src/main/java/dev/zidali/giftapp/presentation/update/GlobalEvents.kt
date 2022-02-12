package dev.zidali.giftapp.presentation.update

sealed class GlobalEvents {

    data class SetNeedToUpdate(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetNeedToUpdateContact(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetNeedToUpdateEventFragment(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetGiftFragmentInView(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetEventFragmentInView(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetEventDetailFragmentView(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetContactFragmentView(
        val boolean: Boolean
    ): GlobalEvents()

    data class SetMultiSelection(
        val boolean: Boolean
    ): GlobalEvents()

}
