package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.toGift
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.Constants.Companion.GIFTS_COLLECTION
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FetchGifts(
    private val giftDao: GiftDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(
        contact_pk: Int,
    ): Flow<DataState<GiftState>> = flow {

        emit(DataState.loading<GiftState>())

        val finalList: MutableList<Gift> = mutableListOf()

        val results = giftDao.getAllGiftByContact(contact_pk).map { it.toGift() }.toMutableList()

        val fireStoreData = fireStore
            .collection(Constants.USERS_COLLECTION)
            .document(firebaseAuth.currentUser!!.uid)
            .collection(Constants.CONTACTS_COLLECTION)
            .document(contact_pk.toString())
            .collection(GIFTS_COLLECTION)
            .get()
            .addOnFailureListener {
                cLog(it.message)
            }
            .await()
            .toObjects(GiftEntity::class.java)

        for(result in results) {
            for(data in fireStoreData) {
                if (result.gift_pk == data.gift_pk) {
                    finalList.add(result)
                }
            }
        }

        val gifts = GiftState(
            contact_gifts = finalList
        )

        emit(DataState.data(
            response = null,
            data = gifts
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}