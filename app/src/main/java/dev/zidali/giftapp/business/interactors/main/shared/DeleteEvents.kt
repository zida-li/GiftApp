package dev.zidali.giftapp.business.interactors.main.shared

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
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
) {

    fun execute(
        contactEvents: List<ContactEvent>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(contactEvent in contactEvents) {

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

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}