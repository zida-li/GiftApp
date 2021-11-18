package dev.zidali.giftapp.presentation.auth.login

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class LoginState (
    var login_email: String? = null,
    var login_password: String? = null,
    var accountProperties: AccountProperties? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class LoginError {
        companion object{

            fun mustFillAllFields(): String{
                return "All fields are required."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForRegistration(): String{
        if(login_email.isNullOrEmpty()
            || login_password.isNullOrEmpty()) {
            return LoginError.mustFillAllFields()
        }

        return LoginError.none()
    }
}