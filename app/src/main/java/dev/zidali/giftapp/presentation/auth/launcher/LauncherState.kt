package dev.zidali.giftapp.presentation.auth.launcher

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class LauncherState (
    val isLoading: Boolean = false,
    var accountProperties: AccountProperties? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)