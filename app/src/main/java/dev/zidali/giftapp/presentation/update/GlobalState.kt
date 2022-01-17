package dev.zidali.giftapp.presentation.update

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class GlobalState(
    val needToUpdateContactPage: Boolean = true,
    val giftFragmentInView: Boolean = false,
    val eventFragmentInView: Boolean = false,
)
