package dev.zidali.giftapp.business.interactors.main.fab

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.notification.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CreateEvent(
    private val contactEventDao: ContactEventDao,
    private val contactDao: ContactDao
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        val contactPk = contactDao.getByName(contactEvent.contact_name)

        val finalContactEvent = ContactEvent(
            contact_name = contactEvent.contact_name,
            contact_event = contactEvent.contact_event,
            contact_event_reminder = contactEvent.contact_event_reminder,
            year = contactEvent.year,
            month = contactEvent.month,
            day = contactEvent.day,
            pk = contactPk?.pk!!,
        )

        contactEventDao.insert(finalContactEvent.toContactEventEntity())

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