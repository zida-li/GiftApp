package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.GiftDao
import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class UpdateContact(
    private val contactDao: ContactDao,
    private val giftDao: GiftDao,
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        contactPk: Int,
        new_name: String,
    ): Flow<DataState<Contact>> = flow <DataState<Contact>>{

        giftDao.updateContactNameGift(new_name, contactPk)
        contactEventDao.updateContactNameEvent(new_name, contactPk)
        contactDao.updateContact(new_name, contactPk)

        emit(DataState.data(
            response = Response(
                message = "Contact Updated",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}