package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.*
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class UpdateContact(
    private val contactDao: ContactDao,
    private val giftDao: GiftDao,
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(
        contactPk: Int,
        new_name: String,
    ): Flow<DataState<Contact>> = flow <DataState<Contact>>{

        /**
         * Updating GiftEntity
         */

        val giftCollectionRef = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contactPk.toString())
            .collection(Constants.GIFTS_COLLECTION)

        giftDao.updateContactNameGift(new_name, contactPk)

        val giftFireStoreData =
            giftCollectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(GiftEntity::class.java)

        for (data in giftFireStoreData) {
            data.contact_name = new_name
            giftCollectionRef
                .document(data.gift_pk.toString())
                .set(data.toGift())
                .await()
        }

        /**
         * Updating ContactEventEntity
         */

        val eventCollectionRef = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contactPk.toString())
            .collection(Constants.EVENTS_COLLECTION)

        contactEventDao.updateContactNameEvent(new_name, contactPk)
        val eventFireStoreData =
            eventCollectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(ContactEventEntity::class.java)

        for (data in eventFireStoreData) {
            data.contact_name = new_name
            eventCollectionRef
                .document(data.event_pk.toString())
                .set(data.toContactEvent())
                .await()
        }

        /**
         * Updating ContactEntity
         */

        val contactCollectionRef = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contactPk.toString())

        contactDao.updateContact(new_name, contactPk)
        val contactFireStoreData =
            contactCollectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObject(ContactEntity::class.java)

        contactFireStoreData?.contact_name = new_name
        contactCollectionRef
            .set(contactFireStoreData!!)
            .await()

        emit(DataState.data(
            response = Response(
                message = "Contact Updated",
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