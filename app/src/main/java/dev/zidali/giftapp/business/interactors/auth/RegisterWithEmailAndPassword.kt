package dev.zidali.giftapp.business.interactors.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toAccountProperties
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.auth.register.RegisterState
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await

class RegisterWithEmailAndPassword(
    private val firebaseAuth: FirebaseAuth,
    private val appDataStore: AppDataStore,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        email: String,
        password: String,
    ): Flow<DataState<RegisterState>> = flow {

            emit(DataState.loading())

            firebaseAuth.createUserWithEmailAndPassword(
                email, password
            ).await()

            if(firebaseAuth.currentUser != null){

                val allAccounts = accountPropertiesDao.getAllUsers().map { it.toAccountProperties() }.toMutableList()

                if(!doesUserExist(firebaseAuth.currentUser!!.email!!, allAccounts)){
                    appDataStore.setValue(DataStoreKeys.CONTACT_FIRST_RUN, "null")
                }

                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        current_authUser_email = firebaseAuth.currentUser?.email!!,
                    ).toEntity()
                )

                val user = RegisterState(
                    accountProperties = AccountProperties(
                        current_authUser_email = firebaseAuth.currentUser?.email!!,
                    )
                )

                fireStore
                    .collection("users")
                    .document(firebaseAuth.currentUser!!.uid)
                    .set(user)
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()

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

    private fun doesUserExist(current_user_email: String, all_existing_users: MutableList<AccountProperties>): Boolean {
        for(user in all_existing_users) {
            if (user.current_authUser_email == current_user_email) {
                return true
            }
        }
        return false
    }

}