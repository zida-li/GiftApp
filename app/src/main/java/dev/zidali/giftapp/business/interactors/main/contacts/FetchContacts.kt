package dev.zidali.giftapp.business.interactors.main.contacts

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContact
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchContacts(
    private val contactDao: ContactDao,
) {

    fun execute(): Flow<DataState<MutableList<Contact>>> = flow {

        val contacts = contactDao.getAllContacts().map { it.toContact() }.toMutableList()

        emit(
            DataState.data(
                response = null,
                data = contacts
            )
        )

    }.catch {e->
        emit(handleUseCaseException(e))
    }

}