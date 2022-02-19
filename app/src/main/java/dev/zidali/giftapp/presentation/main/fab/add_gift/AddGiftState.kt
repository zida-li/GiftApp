package dev.zidali.giftapp.presentation.main.fab.add_gift

import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class AddGiftState(
    var contact_display_list: MutableList<String> = mutableListOf(),
    var contacts: MutableList<Contact> = mutableListOf(),
    var gift: Gift = Gift(contact_name = "", contact_gift = "", pk = 0, gift_pk = 0),
    var contact_name_holder: String = "",
    var contact_pk_holder: Int = 0,
    var contact_gift_holder: String = "",
    var current_contact_name: String = "",
    var addGiftSuccessful: Boolean = false,
    val isLoading: Boolean = false,
    var dataLoaded: Boolean = false,
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