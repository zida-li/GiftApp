package dev.zidali.giftapp.presentation.auth.login

sealed class LoginEvents {

    data class LoginWithGoogle(
        val email: String,
        val password: String,
    ): LoginEvents()

}
