package dev.zidali.giftapp.business.interactors.session

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DeleteAccount(
    private val accountPropertiesDao: AccountPropertiesDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(): Flow<DataState<Response>> = flow<DataState<Response>>{


        if(isOnline()) {

            accountPropertiesDao.deleteUser(firebaseAuth.currentUser!!.email!!)

            fireStore
                .collection(USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .delete()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()

            emit(DataState.data<Response>(
                data = Response(
                    message = SuccessHandling.SUCCESS_DELETE_ACCOUNT,
                    uiComponentType = UIComponentType.Dialog,
                    messageType = MessageType.Error,
                ),
                response = null,
            ))
        } else {
            emit(
                DataState.data(
                    response = Response(
                        message = "You need internet connection to delete your account",
                        messageType = MessageType.Error,
                        uiComponentType = UIComponentType.Dialog,
                    ),
                    data = null,
                )
            )
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

    private fun isOnline(): Boolean {

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if(capabilities != null) {
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            }
        }
        return false
    }

}