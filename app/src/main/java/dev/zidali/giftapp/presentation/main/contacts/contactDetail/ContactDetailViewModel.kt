package dev.zidali.giftapp.presentation.main.contacts.contactDetail

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class ContactDetailViewModel
@Inject
constructor(
    private val sessionManager: SessionManager
): ViewModel() {

    fun onTriggerEvent(event: ContactDetailEvents){
        when (event) {

        }
    }

}