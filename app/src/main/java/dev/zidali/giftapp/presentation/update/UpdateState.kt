package dev.zidali.giftapp.presentation.update

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class UpdateState(
    val needToUpdateContactPage: Boolean = true,
)
