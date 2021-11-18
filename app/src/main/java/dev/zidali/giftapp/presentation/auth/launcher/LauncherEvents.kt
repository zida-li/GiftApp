package dev.zidali.giftapp.presentation.auth.launcher

sealed class LauncherEvents {

    data class LoginWithGoogle(
        val token: String,
    ): LauncherEvents()

}
