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
import java.util.*

class CreateEvent(
    private val contactEventDao: ContactEventDao,
    private val contactDao: ContactDao
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        val contactPk = contactDao.getByName(contactEvent.contact_name)

        val today = Calendar.getInstance()

        val alarmDate = Calendar.getInstance(Locale.getDefault())
        alarmDate.set(Calendar.MONTH, contactEvent.month)
        alarmDate.set(Calendar.DAY_OF_MONTH, contactEvent.day)
        alarmDate.set(Calendar.YEAR, contactEvent.year)

        if (today > alarmDate) {
            contactEvent.expired = true
        }

        val finalContactEvent = ContactEvent(
            contact_name = contactEvent.contact_name,
            contact_event = contactEvent.contact_event,
            contact_event_reminder = contactEvent.contact_event_reminder,
            year = contactEvent.year,
            month = contactEvent.month,
            day = contactEvent.day,
            pk = contactPk?.pk!!,
            ymd_format = contactEvent.ymd_format,
            expired = contactEvent.expired,
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