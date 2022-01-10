package dev.zidali.giftapp.business.interactors.session

import com.google.firebase.auth.FirebaseAuth
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.domain.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class Logout(
    private val firebaseAuth: FirebaseAuth,
) {
    fun execute(): Flow<DataState<Response>> = flow {
        emit(DataState.loading<Response>())
        firebaseAuth.signOut()
        emit(DataState.data<Response>(
            data = Response(
                message = SUCCESS_LOGOUT,
                uiComponentType = UIComponentType.Dialog,
                messageType = MessageType.Error,
            ),
            response = null,
        ))
    }.catch{ e ->
        e.printStackTrace()
        emit(DataState.error<Response>(
            response = Response(
                message = e.message,
                uiComponentType = UIComponentType.Dialog,
                messageType = MessageType.Error,
            )
        ))
    }
}