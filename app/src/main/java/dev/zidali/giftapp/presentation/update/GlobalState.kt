package dev.zidali.giftapp.presentation.update

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class GlobalState(
    val needToUpdate: Boolean = false,
    val needToUpdateEventFragment: Boolean = false,
    val giftFragmentInView: Boolean = false,
    val eventFragmentInView: Boolean = false,
)
