package dev.zidali.giftapp.business.interactors.session

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.provider.ContactsContract
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.GIFTS_COLLECTION
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

            val contactCollectionRef = fireStore
                .collection(USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(CONTACTS_COLLECTION)

            val contacts = contactCollectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(ContactEntity::class.java)

            for(contact in contacts) {

                val giftCollectionRef = fireStore
                    .collection(Constants.USERS_COLLECTION)
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection(Constants.CONTACTS_COLLECTION)
                    .document(contact.pk.toString())
                    .collection(GIFTS_COLLECTION)

                val eventCollectionRef = fireStore
                    .collection(Constants.USERS_COLLECTION)
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection(Constants.CONTACTS_COLLECTION)
                    .document(contact.pk.toString())
                    .collection(Constants.EVENTS_COLLECTION)

                val gifts = giftCollectionRef
                    .get()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()
                    .toObjects(GiftEntity::class.java)

                for(gift in gifts) {
                    giftCollectionRef
                        .document(gift.gift_pk.toString())
                        .delete()
                        .addOnFailureListener {
                            cLog(it.message)
                        }
                        .await()
                }

                val events = eventCollectionRef
                    .get()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()
                    .toObjects(ContactEventEntity::class.java)

                for(event in events) {
                    eventCollectionRef
                        .document(event.event_pk.toString())
                        .delete()
                        .addOnFailureListener {
                            cLog(it.message)
                        }
                        .await()
                }

                contactCollectionRef
                    .document(contact.pk.toString())
                    .delete()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()
            }

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