package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteGifts(
    private val giftDao: GiftDao,
) {

    fun execute(
        gifts: List<Gift>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(gift in gifts) {
            giftDao.deleteGift(gift.toGiftEntity())
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}