package dev.zidali.giftapp.business.interactors.main.fab

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContact
import dev.zidali.giftapp.business.datasource.cache.contacts.toGiftEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Gift
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.main.fab.add_gift.AddGiftState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class AddGift(
    private val giftDao: GiftDao,
    private val contactDao: ContactDao
) {

    fun execute(
        gift: Gift
    ): Flow<DataState<AddGiftState>> = flow <DataState<AddGiftState>>{

        val contactPk = contactDao.getByName(gift.contact_name)?.toContact()

        val editedGift = Gift(
            contact_name = gift.contact_name,
            contact_gift = gift.contact_gift,
            pk = contactPk?.pk!!,
            isChecked = gift.isChecked,
            isMultiSelectionModeEnabled = gift.isMultiSelectionModeEnabled,
        )

        giftDao.insert(editedGift.toGiftEntity())

        emit(DataState.data(
            response = Response(
                message = "${gift.contact_gift} Added",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}