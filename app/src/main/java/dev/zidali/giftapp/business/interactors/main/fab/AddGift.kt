package dev.zidali.giftapp.business.interactors.main.fab

import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.presentation.main.fab.add_gift.AddGiftState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AddGift(
    private val giftDao: GiftDao,
) {

    fun execute(
        gift: Gift
    ): Flow<DataState<AddGiftState>> = flow <DataState<AddGiftState>>{

        giftDao.insert(gift.toGiftEntity())

        emit(DataState.data(
            response = Response(
                message = "${gift.contact_gift} Added",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}