package dev.zidali.giftapp.business.interactors.main.fab

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateEvent(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        contactEventDao.insert(contactEvent.toContactEventEntity())

        emit(DataState.data(
            response = Response(
                message = "${contactEvent.contact_event} Event Created",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}