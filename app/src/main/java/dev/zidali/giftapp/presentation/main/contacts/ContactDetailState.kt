package dev.zidali.giftapp.presentation.main.contacts

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class ContactDetailState(
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)