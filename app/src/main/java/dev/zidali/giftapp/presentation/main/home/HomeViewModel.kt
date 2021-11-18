package dev.zidali.giftapp.presentation.main.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.zidali.giftapp.presentation.session.SessionEvents
import dev.zidali.giftapp.presentation.session.SessionManager
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val sessionManager: SessionManager
): ViewModel() {

    fun onTriggerEvent(event: HomeEvents){
        when (event) {
            is HomeEvents.Logout -> {
                logout()
            }
        }
    }

    private fun logout(){
        sessionManager.onTriggerEvent(SessionEvents.Logout)
    }

}