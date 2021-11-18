package dev.zidali.giftapp.presentation.auth.login

import dev.zidali.giftapp.business.domain.util.StateMessage
import dev.zidali.giftapp.presentation.auth.register.RegisterEvents

sealed class LoginEvents {

    object GetEmail: LoginEvents()

    object LoginWithGoogle: LoginEvents()

    data class OnUpdateEmail(
        val email: String
    ): LoginEvents()

    data class OnUpdatePassword(
        val password: String
    ): LoginEvents()

    object SaveLoginState: LoginEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): LoginEvents()

    object OnRemoveHeadFromQueue: LoginEvents()

}
