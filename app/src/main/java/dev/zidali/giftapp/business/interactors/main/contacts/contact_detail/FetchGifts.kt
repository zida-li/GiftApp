package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.toGift
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftState
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.GIFT_FIRST_RUN
import dev.zidali.giftapp.presentation.util.DataStoreKeys.Companion.GIFT_UPDATED
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.GIFTS_COLLECTION
import dev.zidali.giftapp.util.Constants.Companion.TAG
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchGifts(
    private val giftDao: GiftDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
    private val appDataStore: AppDataStore,
) {

    fun execute(
        contact_pk: Int,
    ): Flow<DataState<GiftState>> = flow {

        emit(DataState.loading<GiftState>())

        val finalList: MutableList<Gift> = mutableListOf()

        val collectionRef = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contact_pk.toString())
            .collection(GIFTS_COLLECTION)

        if(isOnline()) {

            val results = giftDao.getAllGiftByContact(contact_pk).map { it.toGift() }.toMutableList()

            val fireStoreData =
                collectionRef
                .get()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()
                .toObjects(GiftEntity::class.java)

            val fireStoreGift = fireStoreData.map { it.toGift() }.toMutableList()

            for(result in results) {
                if(fireStoreGift.contains(result)) {
                    finalList.add(result)
                } else {
                    if(isRoomEntityNew(result, fireStoreGift)) {
                        finalList.add(result)
                        collectionRef
                            .add(result)
                            .addOnFailureListener {
                                cLog(it.message)
                            }
                            .await()
                    } else {
                        finalList.add(result)
                        collectionRef
                            .document(result.gift_pk.toString())
                            .set(result.toGiftEntity())
                            .addOnFailureListener {
                                cLog(it.message)
                            }
                            .await()
                    }
                }
            }

            val wasGiftDeletedOffline = appDataStore.readValue(GIFT_UPDATED)

            if(wasGiftDeletedOffline == "true") {
                appDataStore.setValue(GIFT_UPDATED, "false")
                for (gift in fireStoreGift) {
                    if (!results.contains(gift)) {
                        if (doesFirebaseEntityMatch(gift, results)) {
                            //do nothing, previous function took care of this
                        } else {
                            collectionRef
                                .document(gift.gift_pk.toString())
                                .delete()
                                .addOnFailureListener {
                                    cLog(it.message)
                                }
                                .await()
                        }
                    }
                }
            }

        } else {
            val results = giftDao.getAllGiftByContact(contact_pk).map { it.toGift() }.toMutableList()
            finalList.addAll(results)
        }

        val gifts = GiftState(
            contact_gifts = finalList
        )

        emit(DataState.data(
            response = null,
            data = gifts
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

    private fun isRoomEntityNew(gift: Gift, fireBaseList: MutableList<Gift>): Boolean {
        for (fire in fireBaseList) {
            if(gift.gift_pk == fire.gift_pk) {
                return false
            }
        }
        return true
    }

    private fun doesFirebaseEntityMatch(gift: Gift, roomList: MutableList<Gift>): Boolean {
        for (room in roomList) {
            if(gift.gift_pk == room.gift_pk) {
                return true
            }
        }
        return false
    }


}