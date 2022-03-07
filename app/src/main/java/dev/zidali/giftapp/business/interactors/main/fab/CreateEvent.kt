package dev.zidali.giftapp.business.interactors.main.fab

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.*
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventState
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.EVENTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*

class CreateEvent(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<CreateEventState>> = flow<DataState<CreateEventState>> {

        val today = Calendar.getInstance()

        val alarmDate = Calendar.getInstance(Locale.getDefault())
        alarmDate.set(Calendar.MONTH, contactEvent.month)
        alarmDate.set(Calendar.DAY_OF_MONTH, contactEvent.day)
        alarmDate.set(Calendar.YEAR, contactEvent.year)

        if (today > alarmDate) {
            contactEvent.expired = true
        }

        contactEvent.event_owner = firebaseAuth.currentUser!!.email!!

        val pk = contactEventDao.insert(contactEvent.toContactEventEntity())

        contactEvent.event_pk = pk.toInt()

        fireStore
            .collection(USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(CONTACTS_COLLECTION)
            .document(contactEvent.contact_pk.toString())
            .collection(EVENTS_COLLECTION)
            .document(contactEvent.event_pk.toString())
            .set(contactEvent.toContactEventEntity())
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()


        emit(DataState.data(
            response = Response(
                message = "${contactEvent.contact_event} Event Created",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            ),
            data = CreateEventState(
                new_event_pk_holder = pk.toInt()
            )
        ))

    }.catch { e->
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

}