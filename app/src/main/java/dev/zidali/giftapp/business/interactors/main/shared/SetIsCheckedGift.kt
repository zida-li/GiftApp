package dev.zidali.giftapp.business.interactors.main.shared

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class SetIsCheckedGift(
    private val giftDao: GiftDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
    private val connectivityManager: ConnectivityManager,
) {

    fun execute(
        gift: Gift
    ): Flow<DataState<Gift>> = flow<DataState<Gift>> {

        giftDao.updateIsChecked(gift.isChecked, gift.gift_pk)

        fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(gift.contact_pk.toString())
            .collection(Constants.GIFTS_COLLECTION)
            .document(gift.gift_pk.toString())
            .set(gift.toGiftEntity())
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()

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