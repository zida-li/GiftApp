package dev.zidali.giftapp.presentation.auth.login

import dev.zidali.giftapp.business.domain.util.Queue
import dev.zidali.giftapp.business.domain.util.StateMessage

data class RegisterState (
    var registrationFields: RegistrationFields? = RegistrationFields(),
    val queue: Queue<StateMessage> = Queue(mutableListOf())
)

data class RegistrationFields(
    var registration_email: String? = null,
    var registration_password: String? = null,
    var registration_confirm_password: String? = null
){

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
        if(registration_email.isNullOrEmpty()
            || registration_password.isNullOrEmpty()
            || registration_confirm_password.isNullOrEmpty()){
            return RegistrationError.mustFillAllFields()
        }

        if(!registration_password.equals(registration_confirm_password)){
            return RegistrationError.passwordsDoNotMatch()
        }
        return RegistrationError.none()
    }
}