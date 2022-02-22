package dev.zidali.giftapp.business.interactors.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.auth.launcher.LauncherState
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LoginWithGoogle(
    private val firebaseAuth: FirebaseAuth,
    private val accountPropertiesDao: AccountPropertiesDao,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        idToken: String,
    ): Flow<DataState<LauncherState>> = flow {

        emit(DataState.loading<LauncherState>())

        firebaseAuth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null)).await()

        if(firebaseAuth.currentUser != null) {

            accountPropertiesDao.insertAndReplace(
                AccountProperties(
                    current_authUser_email = firebaseAuth.currentUser?.email!!
                ).toEntity()
            )

            val user = LauncherState(
                accountProperties = AccountProperties(
                    current_authUser_email = firebaseAuth.currentUser?.email!!
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

            emit(DataState.data(
                response = null,
                data = user
            ))
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}