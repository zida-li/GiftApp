package dev.zidali.giftapp.presentation.update

sealed class UpdateEvents {

    object UpdateComplete: UpdateEvents()

    object RequestUpdate: UpdateEvents()

}
