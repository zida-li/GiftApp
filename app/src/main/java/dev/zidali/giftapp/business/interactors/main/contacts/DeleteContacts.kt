package dev.zidali.giftapp.business.interactors.main.contacts

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactsEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteContacts(
    private val contactDao: ContactDao,
) {

    fun execute(
        contacts: List<Contact>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(contact in contacts) {
            contactDao.deleteContacts(contact.toContactsEntity())
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}