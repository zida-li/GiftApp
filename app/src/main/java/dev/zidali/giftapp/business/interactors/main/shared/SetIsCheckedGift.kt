package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class SetIsCheckedGift(
    private val giftDao: GiftDao,
) {

    fun execute(
        gift: Gift
    ): Flow<DataState<Gift>> = flow<DataState<Gift>> {

        giftDao.updateIsChecked(gift.isChecked, gift.gift_pk)

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}