package dev.zidali.giftapp.presentation.main.contacts

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class ContactsViewModel
@Inject
constructor(
    private val sessionManager: SessionManager
): ViewModel() {

    fun onTriggerEvent(event: ContactsEvents){
        when (event) {
            is ContactsEvents.Logout -> {
                logout()
            }
        }
    }

    private fun logout(){
        sessionManager.onTriggerEvent(SessionEvents.Logout)
    }

}