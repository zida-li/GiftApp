package dev.zidali.giftapp.business.interactors.auth

import com.google.firebase.auth.FirebaseAuth
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.auth.register.RegisterState
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class RegisterWithEmailAndPassword(
    private val firebaseAuth: FirebaseAuth,
    private val appDataStore: AppDataStore,
    private val accountPropertiesDao: AccountPropertiesDao,
) {

    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<RegisterState>> = flow {

            firebaseAuth.createUserWithEmailAndPassword(
                email, password
            ).await()

            if(firebaseAuth.currentUser != null){

                accountPropertiesDao.insertAndReplace(
                    AccountProperties(
                        current_authUser_email = firebaseAuth.currentUser?.email!!
                    ).toEntity()
                )

                val user = RegisterState(
                    accountProperties = AccountProperties(
                        current_authUser_email = firebaseAuth.currentUser?.email!!,
                    )
                )

                appDataStore.setValue(DataStoreKeys.REGISTRATION_EMAIL, "")
                appDataStore.setValue(DataStoreKeys.LOGIN_EMAIL, user.accountProperties!!.current_authUser_email)

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