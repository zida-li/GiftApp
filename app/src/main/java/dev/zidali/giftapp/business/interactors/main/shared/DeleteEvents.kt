package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.EVENT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DeleteEvents(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        contactEvents: List<ContactEvent>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        if(isOnline()) {
            for (contactEvent in contactEvents) {

                fireStore
                    .collection(Constants.USERS_COLLECTION)
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection(Constants.CONTACTS_COLLECTION)
                    .document(contactEvent.pk.toString())
                    .collection(Constants.EVENTS_COLLECTION)
                    .document(contactEvent.event_pk.toString())
                    .delete()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()

                contactEventDao.deleteEvent(contactEvent.toContactEventEntity())
            }
        } else {
            appDataStore.setValue(EVENT_UPDATED, "true")
            for(contactEvent in contactEvents) {
                contactEventDao.deleteEvent(contactEvent.toContactEventEntity())
            }
        }

    }.catch { e->
        Log.d(Constants.TAG, e.toString())
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