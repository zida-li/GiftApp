package dev.zidali.giftapp.presentation.edit.event_detail

import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class EventDetailState(
    val contact_event: ContactEvent? = null,
    val event_pk: String = "",
    val contact_pk: String = "",
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)