package dev.zidali.giftapp.presentation.main.fab.create_contact

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class CreateContactState(
    var name: String = "",
    var contact: Contact = Contact(),
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class CreateContactError {
        companion object{

            fun mustFillAllFields(): String{
                return "Name cannot be blank"
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValid(): String{
        if(name.isEmpty()) {
            return CreateContactError.mustFillAllFields()
        }

        return CreateContactError.none()
    }
}