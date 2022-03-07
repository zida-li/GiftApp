package dev.zidali.giftapp.business.interactors.main.shared

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.*
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.CONTACT_FIRST_RUN
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.CONTACT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.EVENTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.GIFTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.coroutineContext

class FetchContacts(
    private val contactDao: ContactDao,
    private val giftDao: GiftDao,
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        email: String,
        context: Context,
    ): Flow<DataState<MutableList<Contact>>> = flow {

        emit(DataState.loading<MutableList<Contact>>())

        val finalList: MutableList<Contact> = mutableListOf()

            val contactCollectionRef = fireStore
                .collection(USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(CONTACTS_COLLECTION)

            val fireStoreContacts = contactCollectionRef
                .get()
                .addOnFailureListener{
                    cLog(it.message)
                }
                .await()
                .toObjects(ContactEntity::class.java)

            finalList.addAll(fireStoreContacts.map { it.toContact()})
            for(contact in fireStoreContacts) {
                contactDao.insert(contact)
            }

        //Update room from firebase
        for (contact in fireStoreContacts) {
            val gifts = fireStore
                .collection(USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(CONTACTS_COLLECTION)
                .document(contact.contact_pk.toString())
                .collection(GIFTS_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(GiftEntity::class.java)

            for(gift in gifts) {
                giftDao.insert(gift)
            }

            val events = fireStore
                .collection(Constants.USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(Constants.CONTACTS_COLLECTION)
                .document(contact.contact_pk.toString())
                .collection(Constants.EVENTS_COLLECTION)
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(ContactEventEntity::class.java)

            for(event in events) {
                contactEventDao.insert(event)
                AlarmScheduler.scheduleInitialAlarmsForReminder(context, event.toContactEvent())
            }
        }

        emit(
            DataState.data(
                response = null,
                data = finalList
            )
        )

    }.catch {e->
        Log.d(TAG, e.toString())
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

    private fun isRoomEntityNew(result: Contact, fireBaseList: MutableList<Contact>): Boolean {
        for (fire in fireBaseList) {
            if(result.contact_pk == fire.contact_pk) {
                return false
            }
        }
        return true
    }

    private fun doesFirebaseEntityMatch(event: Contact, roomList: MutableList<Contact>): Boolean {
        for (room in roomList) {
            if(room.contact_pk == event.contact_pk) {
                return true
            }
        }
        return false
    }

}