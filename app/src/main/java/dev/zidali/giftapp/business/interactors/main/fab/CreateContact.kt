package dev.zidali.giftapp.business.interactors.main.fab

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await


class CreateContact(
    private val contactDao: ContactDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        contact: Contact
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        val pk = contactDao.insert(contact.toContactsEntity())

        contact.pk = pk.toInt()

        fireStore
            .collection(USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(CONTACTS_COLLECTION)
            .document(contact.pk.toString())
            .set(contact.toContactsEntity())
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()

        emit(
            DataState.data(
                response = Response(
                    message = "${contact.contact_name} Added To Contacts",
                    uiComponentType = UIComponentType.None,
                    messageType = MessageType.None,
                ),
                data = null
            )
        )

    }.catch { e->
        Log.d(TAG, e.toString())
    }

}