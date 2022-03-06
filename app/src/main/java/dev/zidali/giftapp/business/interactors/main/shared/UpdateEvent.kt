package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*

class UpdateEvent(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(
        updatedEvent: ContactEvent,
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        val today = Calendar.getInstance()

        val alarmDate = Calendar.getInstance(Locale.getDefault())
        alarmDate.set(Calendar.MONTH, updatedEvent.month)
        alarmDate.set(Calendar.DAY_OF_MONTH, updatedEvent.day)
        alarmDate.set(Calendar.YEAR, updatedEvent.year)

        updatedEvent.expired = today > alarmDate

            contactEventDao.updateContactEvent(
                updatedEvent.contact_event,
                updatedEvent.contact_event_reminder,
                updatedEvent.year,
                updatedEvent.month,
                updatedEvent.day,
                updatedEvent.ymd_format,
                updatedEvent.expired,
                updatedEvent.event_pk,
            )

            fireStore
                .collection(Constants.USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(Constants.CONTACTS_COLLECTION)
                .document(updatedEvent.contact_pk.toString())
                .collection(Constants.EVENTS_COLLECTION)
                .document(updatedEvent.event_pk.toString())
                .set(updatedEvent.toContactEventEntity())
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()

        emit(DataState.data(
            response = Response(
                message = "Event Updated",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            )
        ))

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