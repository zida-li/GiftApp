package dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class GiftState(
    val contact_pk: Int = 0,
    var contact_gifts: MutableList<Gift> = mutableListOf(),
    val firstLoad: Boolean = true,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)