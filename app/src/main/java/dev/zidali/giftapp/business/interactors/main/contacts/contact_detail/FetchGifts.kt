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

        val results = giftDao.getAllGiftByContact(contact_pk).map { it.toGift() }.toMutableList()

        val gifts = GiftState(
            contact_gifts = results
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