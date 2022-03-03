package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.*
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.CONTACT_FIRST_RUN
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.CONTACT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.CONTACTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.EVENTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.GIFTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.Constants.Companion.USERS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchContacts(
    private val contactDao: ContactDao,
    private val giftDao: GiftDao,
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        email: String,
    ): Flow<DataState<MutableList<Contact>>> = flow {

        val finalList: MutableList<Contact> = mutableListOf()

        emit(DataState.loading<MutableList<Contact>>())

        if(isOnline()) {

            val results = contactDao.getAllContactOfUser(email).map { it.toContact() }.toMutableList()

            val contactCollectionRef = fireStore
                .collection(USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(CONTACTS_COLLECTION)

            val fireStoreData = contactCollectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(ContactEntity::class.java)

            val fireStoreToContact = fireStoreData.map { it.toContact() }.toMutableList()

            for(result in results) {
                if(fireStoreToContact.contains(result)) {
                    finalList.add(result)
                } else {
                    //the result that goes here have either been created or updated while offline.
                    if(isRoomEntityNew(result, fireStoreToContact)) {
                        //if true, the contact is new so we need to add a new contact in firebase.
                        finalList.add(result)
                        contactCollectionRef
                            .add(result.toContactsEntity())
                            .addOnFailureListener {
                                cLog(it.message)
                            }
                            .await()
                    } else {
                        //if anything ends up here, it means the contact already exists so we just need to update it.
                        finalList.add(result)
                        //updates contact
                        contactCollectionRef
                            .document(result.contact_pk.toString())
                            .set(result)
                            .addOnFailureListener {
                                cLog(it.message)
                            }
                            .await()

                        val giftCollectionRef = fireStore
                            .collection(USERS_COLLECTION)
                            .document(firebaseAuth.currentUser!!.uid)
                            .collection(CONTACTS_COLLECTION)
                            .document(result.contact_pk.toString())
                            .collection(GIFTS_COLLECTION)

                        val eventCollectionRef = fireStore
                            .collection(USERS_COLLECTION)
                            .document(firebaseAuth.currentUser!!.uid)
                            .collection(CONTACTS_COLLECTION)
                            .document(result.contact_pk.toString())
                            .collection(EVENTS_COLLECTION)

                        //updates contact name in GiftEntity
                        val giftFireStoreData =
                            giftCollectionRef
                            .get()
                            .addOnFailureListener {
                                cLog(it.message)
                            }
                            .await()
                            .toObjects(GiftEntity::class.java)

                        for(gift in giftFireStoreData){
                            gift.contact_name = result.contact_name!!
                            giftCollectionRef
                                .document(gift.gift_pk.toString())
                                .set(gift.toGift())
                                .await()
                        }

                        //updates contact name in ContactEventEntity
                        val eventFireStoreData =
                            eventCollectionRef
                                .get()
                                .addOnFailureListener {
                                    cLog(it.message)
                                }
                                .await()
                                .toObjects(ContactEventEntity::class.java)

                        for(event in eventFireStoreData) {
                            event.contact_name = result.contact_name!!
                            eventCollectionRef
                                .document(event.event_pk.toString())
                                .set(event.toContactEvent())
                                .await()
                        }
                    }
                }
            }

            val wasContactDeletedOffline = appDataStore.readValue(CONTACT_UPDATED)

            if(wasContactDeletedOffline == "true") {
                appDataStore.setValue(CONTACT_UPDATED, "false")
                //the value will only be set to true when contact was updated offline.
                for (contact in fireStoreToContact) {
                    if (!results.contains(contact)) {
                        if (doesFirebaseEntityMatch(contact, results)) {
                            //do nothing, already taken care of by previous function
                        } else {
                            //if anything ends up here, it means contact was deleted offline. So must delete from firebase.
                            contactCollectionRef
                                .document(contact.contact_pk.toString())
                                .delete()
                                .addOnFailureListener {
                                    cLog(it.message)
                                }
                                .await()
                        }
                    }
                }
            }

            //first launch
            if(appDataStore.readValue(CONTACT_FIRST_RUN) == null ||
               appDataStore.readValue(CONTACT_FIRST_RUN) == "null") {
                appDataStore.setValue(CONTACT_FIRST_RUN, "completed")

//                Log.d(TAG, "FetchContacts: first launch")
                //Take all contacts from firebase and add them to room.
                val contacts = contactCollectionRef
                    .get()
                    .addOnFailureListener {
                        cLog(it.message)
                    }
                    .await()
                    .toObjects(ContactEntity::class.java)

                for(contact in contacts) {
                    contactDao.insert(contact)
                }

                val firstResults = contactDao.getAllContactOfUser(email).map { it.toContact() }.toMutableList()
                finalList.addAll(firstResults)

                //Take all gifts of all contacts from firebase and add them to room.
                for (contact in contacts) {
                    val gifts = fireStore
                        .collection(USERS_COLLECTION)
                        .document(firebaseAuth.currentUser!!.uid)
                        .collection(CONTACTS_COLLECTION)
                        .document(contact.contact_pk.toString())
                        .collection(GIFTS_COLLECTION)
                        .get()
                        .addOnFailureListener {
                            cLog(it.message)
                        }
                        .await()
                        .toObjects(GiftEntity::class.java)

                    for(gift in gifts) {
                        giftDao.insert(gift)
                    }
                }

                //Take all events of all contacts from firebase and add them to room.
                for(contact in contacts){
                    val events = fireStore
                        .collection(Constants.USERS_COLLECTION)
                        .document(firebaseAuth.currentUser!!.uid)
                        .collection(Constants.CONTACTS_COLLECTION)
                        .document(contact.contact_pk.toString())
                        .collection(Constants.EVENTS_COLLECTION)
                        .get()
                        .addOnFailureListener {
                            cLog(it.message)
                        }
                        .await()
                        .toObjects(ContactEventEntity::class.java)

                    for(event in events) {
                        contactEventDao.insert(event)
                    }
                }
            }

        } else {
            val results = contactDao.getAllContactOfUser(email).map { it.toContact() }.toMutableList()
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

    private fun isRoomEntityNew(result: Contact, fireBaseList: MutableList<Contact>): Boolean {
        for (fire in fireBaseList) {
            if(result.contact_pk == fire.contact_pk) {
                return false
            }
        }
        return true
    }

    private fun doesFirebaseEntityMatch(event: Contact, roomList: MutableList<Contact>): Boolean {
        for (room in roomList) {
            if(room.contact_pk == event.contact_pk) {
                return true
            }
        }
        return false
    }

}