package dev.zidali.giftapp.business.interactors.main.contacts

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.CONTACT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DeleteContacts(
    private val contactDao: ContactDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        contacts: List<Contact>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        if(isOnline()) {
            for (contact in contacts) {

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
        } else {
            appDataStore.setValue(CONTACT_UPDATED, "true")
            for (contact in contacts) {
                contactDao.deleteContacts(contact.toContactsEntity())
            }
        }

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