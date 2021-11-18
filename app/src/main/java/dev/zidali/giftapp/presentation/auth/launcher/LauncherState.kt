package dev.zidali.giftapp.presentation.auth.launcher

import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class LauncherState (
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)