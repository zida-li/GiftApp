package dev.zidali.giftapp.presentation.main.contacts.contact_detail.event

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class EventState(
    val contact_pk: String = "",
    var contact_events: MutableList<ContactEvent> = mutableListOf(),
    val contact_event_holder: ContactEvent? = null,
    val isLoading: Boolean = false,
    val firstLoad: Boolean = true,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)