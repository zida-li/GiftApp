package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class EventState(
    val contact_name: String = "",
    var contact_events: MutableList<ContactEvent> = mutableListOf(),
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)