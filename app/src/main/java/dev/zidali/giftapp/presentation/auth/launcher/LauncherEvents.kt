package dev.zidali.giftapp.presentation.auth.launcher

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class LauncherEvents {

    data class LoginWithGoogle(
        val token: String,
    ): LauncherEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): LauncherEvents()

    object OnRemoveHeadFromQueue: LauncherEvents()

}
