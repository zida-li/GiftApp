package dev.zidali.giftapp.presentation.main.all_events

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class AllEventState(
    val contact_name: String = "",
    var contact_events: MutableList<ContactEvent> = mutableListOf(),
    val contact_event_holder: ContactEvent? = null,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)