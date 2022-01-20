package dev.zidali.giftapp.presentation.main.contacts

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class ContactState(
    var contactList: MutableList<Contact> = mutableListOf(),
    val firstLoad: Boolean = true,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)