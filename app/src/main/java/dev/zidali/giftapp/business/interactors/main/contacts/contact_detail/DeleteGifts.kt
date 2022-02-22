package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DeleteGifts(
    private val giftDao: GiftDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        gifts: List<Gift>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(gift in gifts) {

            fireStore
                .collection(Constants.USERS_COLLECTION)
                .document(firebaseAuth.currentUser!!.uid)
                .collection(Constants.CONTACTS_COLLECTION)
                .document(gift.pk.toString())
                .collection(Constants.GIFTS_COLLECTION)
                .document(gift.gift_pk.toString())
                .delete()
                .addOnFailureListener {
                    cLog(it.message)
                }
                .await()

            giftDao.deleteGift(gift.toGiftEntity())
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}