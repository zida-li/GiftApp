package dev.zidali.giftapp.presentation.auth.register

import dev.zidali.giftapp.business.domain.util.StateMessage

sealed class RegisterEvents {

    object RegisterWithGoogle: RegisterEvents()

    data class OnUpdateEmail(
        val email: String
    ): RegisterEvents()

    data class OnUpdatePassword(
        val password: String
    ): RegisterEvents()

    data class OnUpdateConfirmPassword(
        val confirmPassword: String
    ): RegisterEvents()

    object SaveRegisterState: RegisterEvents()

    object GetEmail: RegisterEvents()

    data class AppendToMessageQueue(
        val stateMessage: StateMessage
    ): RegisterEvents()

    object OnRemoveHeadFromQueue: RegisterEvents()

}
