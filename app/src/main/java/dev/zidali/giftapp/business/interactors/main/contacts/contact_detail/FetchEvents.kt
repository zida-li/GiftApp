package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.*
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventState
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.EVENT_FIRST_RUN
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.EVENT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchEvents(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        contact_pk: Int,
    ): Flow<DataState<EventState>> = flow {

        emit(DataState.loading<EventState>())

        val results = contactEventDao.getAllEventsOfContact(contact_pk).map { it.toContactEvent() }.toMutableList()

        val events = EventState(
            contact_events = results
        )

        emit(DataState.data(
            response = null,
            data = events
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

    private fun isRoomEntityNew(result: ContactEvent, fireBaseList: MutableList<ContactEvent>): Boolean {
        for (fire in fireBaseList) {
            if(result.event_pk == fire.event_pk) {
                return false
            }
        }
        return true
    }

    private fun doesFirebaseEntityMatch(event: ContactEvent, roomList: MutableList<ContactEvent>): Boolean {
        for (room in roomList) {
            if(room.event_pk == event.event_pk) {
                return true
            }
        }
        return false
    }

}