package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class ContactDetailState(
    val contact_name: String = "",
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)