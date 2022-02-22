package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchEvents(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        contact_pk: Int,
    ): Flow<DataState<EventState>> = flow {

        emit(DataState.loading<EventState>())

        val finalList: MutableList<ContactEvent> = mutableListOf()

        val results = contactEventDao.getAllEventsOfContact(contact_pk).map { it.toContactEvent() }.toMutableList()

        val fireStoreData = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contact_pk.toString())
            .collection(Constants.EVENTS_COLLECTION)
            .get()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
            .toObjects(ContactEventEntity::class.java)

        for(result in results) {
            for(data in fireStoreData) {
                if(result.event_pk == data.event_pk) {
                    finalList.add(result)
                }
            }
        }

        val events = EventState(
            contact_events = finalList
        )

        emit(DataState.data(
            response = null,
            data = events
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}