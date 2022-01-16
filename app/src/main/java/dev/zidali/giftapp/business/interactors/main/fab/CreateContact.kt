package dev.zidali.giftapp.business.interactors.main.fab

import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow


class CreateContact(
    private val contactDao: ContactDao,
) {

    fun execute(
        contact: Contact
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        contactDao.insert(contact.toContactsEntity())

        emit(
            DataState.data(
                response = Response(
                    message = "${contact.contact_name} Added To Contacts",
                    uiComponentType = UIComponentType.None,
                    messageType = MessageType.None,
                ),
                data = null
            )
        )

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}