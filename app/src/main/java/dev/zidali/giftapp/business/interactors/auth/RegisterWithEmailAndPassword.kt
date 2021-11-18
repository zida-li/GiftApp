package dev.zidali.giftapp.business.interactors.auth

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.presentation.auth.register.RegisterState
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.coroutineContext

class RegisterWithEmailAndPassword(
    private val firebaseAuth: FirebaseAuth,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<RegisterState>> = flow {

            firebaseAuth.createUserWithEmailAndPassword(
                email, password
            ).await()

            val user = RegisterState(
                accountProperties = AccountProperties(
                    email = firebaseAuth.currentUser?.email!!,
                    displayName = firebaseAuth.currentUser?.displayName!!,
                )
            )

            appDataStore.setValue(DataStoreKeys.REGISTRATION_EMAIL, "")
            appDataStore.setValue(DataStoreKeys.LOGIN_EMAIL, user.accountProperties!!.email)

            emit(
                DataState.data(
                    response = null,
                    data = user
                )
            )

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}