package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class GiftState(
    val contact_name: String = "",
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)