package dev.zidali.giftapp.business.interactors.main.shared

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
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


class UpdateContactEventReminder(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contactEvent.contact_pk.toString())
            .collection(Constants.EVENTS_COLLECTION)
            .document(contactEvent.event_pk.toString())
            .set(contactEvent.toContactEventEntity())
            .addOnFailureListener{
                cLog(it.message)
            }
            .await()

        contactEventDao.updateContactReminder(
            contactEvent.contact_event_reminder,
            contactEvent.contact_pk,
            contactEvent.event_pk
        )

        emit(
            DataState.data(
                response = Response(
                    message = "Notification Updated",
                    uiComponentType = UIComponentType.Toast,
                    messageType = MessageType.None
                )
            )
        )

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}