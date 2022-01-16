package dev.zidali.giftapp.presentation.main.fab.add_gift

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class AddGiftState(
    var contacts: MutableList<String> = mutableListOf(),
    var gift: Gift = Gift(contact_name = "", contact_gift = ""),
    var contact_name_holder: String = "",
    var contact_gift_holder: String ="",
    var addGiftSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class CreateGiftError {
        companion object{

            fun mustFillAllFields(): String{
                return "Gift cannot be blank"
            }

            fun mustSelectContact(): String {
                return "You must select a contact"
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValid(): String{
        if(gift.contact_gift == "") {
            return CreateGiftError.mustFillAllFields()
        }
        if (gift.contact_name == "Select Contact") {
            return CreateGiftError.mustSelectContact()
        }

        return CreateGiftError.none()
    }
}