package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.Contact
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class DeleteEvents(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        contactEvents: List<ContactEvent>
    ): Flow<DataState<Contact>> = flow<DataState<Contact>> {

        for(contactEvent in contactEvents) {
            contactEventDao.deleteEvent(contactEvent.toContactEventEntity())
        }

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}