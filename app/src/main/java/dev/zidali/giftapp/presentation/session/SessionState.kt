package dev.zidali.giftapp.presentation.session

import dev.zidali.giftapp.business.domain.models.AuthToken
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class SessionState(
    val isLoading: Boolean = false,
    val authToken: AuthToken? = null,
    val didCheckForPreviousAuthUser: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
)
