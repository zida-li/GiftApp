package dev.zidali.giftapp.presentation.main.contacts.contact_detail

import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class ContactDetailState(
    val contact_name: String = "",
    val changed_name: String ="",
    val contact_pk: Int = 0,
    var isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)