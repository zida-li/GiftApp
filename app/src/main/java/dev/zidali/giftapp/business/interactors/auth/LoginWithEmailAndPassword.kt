package dev.zidali.giftapp.business.interactors.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.auth.login.LoginState
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LoginWithEmailAndPassword(
    private val firebaseAuth: FirebaseAuth,
) {

    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<LoginState>> = flow {

        firebaseAuth.signInWithEmailAndPassword(
            email, password
        ).await()

        if(firebaseAuth.currentUser != null) {

            val user = LoginState(
                accountProperties = AccountProperties(
                    email = firebaseAuth.currentUser?.email!!
                )
            )

            emit(
                DataState.data(
                    response = null,
                    data = user
                )
            )

        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}