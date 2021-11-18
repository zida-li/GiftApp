package dev.zidali.giftapp.presentation.session

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.models.AuthToken

sealed class SessionEvents {

    object Logout: SessionEvents()

    data class Login(
        val accountProperties: AccountProperties
    ): SessionEvents()

    object CheckPreviousAuthUser: SessionEvents()

    object OnRemoveHeadFromQueue: SessionEvents()

}
