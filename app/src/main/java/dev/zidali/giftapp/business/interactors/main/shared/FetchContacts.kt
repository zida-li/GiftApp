package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.toContact
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchContacts(
    private val contactDao: ContactDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(): Flow<DataState<MutableList<Contact>>> = flow {

        emit(DataState.loading<MutableList<Contact>>())

        val finalList: MutableList<Contact> = mutableListOf()

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        if(capabilities != null) {
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {

                val results = contactDao.getAllContacts().map { it.toContact() }.toMutableList()

                val collectionRef = fireStore
                    .collection(USERS_COLLECTION)
                    .document(firebaseAuth.currentUser!!.uid)
                    .collection(CONTACTS_COLLECTION)

                val fireStoreData = collectionRef
                    .get()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()
                    .toObjects(ContactEntity::class.java)

                for(result in results) {
                    for(data in fireStoreData) {
                        if (result.pk == data.pk) {
                            finalList.add(result)
                        } else {
                            //in room but not firebase, so user added contact while offline. Need to add to firebase.
                            collectionRef
                                .document(result.pk.toString())
                                .set(result.toContactsEntity())
                                .addOnFailureListener {
                                    cLog(it.message)
                                }
                                .await()
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "FetchContacts(): else")
            val results = contactDao.getAllContacts().map { it.toContact() }.toMutableList()
            finalList.addAll(results)
        }

        emit(
            DataState.data(
                response = null,
                data = finalList
            )
        )

    }.catch {e->
        Log.d(TAG, e.toString())
    }

}