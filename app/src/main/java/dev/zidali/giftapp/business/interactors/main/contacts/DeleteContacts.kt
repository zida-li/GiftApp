package dev.zidali.giftapp.business.interactors.main.contacts

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DeleteContacts(
    private val contactDao: ContactDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        contacts: List<Contact>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(contact in contacts) {

            fireStore
                .collection(Constants.USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(Constants.CONTACTS_COLLECTION)
                .document(contact.pk.toString())
                .delete()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()

            contactDao.deleteContacts(contact.toContactsEntity())
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}