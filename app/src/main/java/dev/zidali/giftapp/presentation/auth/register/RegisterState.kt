package dev.zidali.giftapp.presentation.auth.register

import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class RegisterState (
    var registration_email: String = "",
    var registration_password: String = "",
    var registration_confirm_password: String = "",
    var accountProperties: AccountProperties? = null,
    val isLoading: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf())
) {

    class RegistrationError {

        companion object{

            fun mustFillAllFields(): String{
                return "All fields are required."
            }

            fun passwordsDoNotMatch(): String{
                return "Passwords must match."
            }

            fun none():String{
                return "None"
            }

        }
    }

    fun isValidForRegistration(): String{
        if(registration_email.isEmpty()
            || registration_password.isEmpty()
            || registration_confirm_password.isEmpty()){
            return RegistrationError.mustFillAllFields()
        }

        if(registration_password != registration_confirm_password){
            return RegistrationError.passwordsDoNotMatch()
        }

        return RegistrationError.none()
    }
}