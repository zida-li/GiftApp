package dev.zidali.giftapp.business.interactors.main.contacts.contact_detail

import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toGift
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.gift.GiftState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchGifts(
    private val giftDao: GiftDao,
) {

    fun execute(
        contact_name: String,
    ): Flow<DataState<GiftState>> = flow {

        val results = giftDao.getAllGiftByContact(contact_name).map { it.toGift() }.toMutableList()

        val gifts = GiftState(
            contact_gifts = results
        )

        emit(DataState.data(
            response = null,
            data = gifts
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}