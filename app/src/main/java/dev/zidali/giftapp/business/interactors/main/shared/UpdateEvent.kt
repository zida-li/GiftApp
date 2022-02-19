package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class UpdateEvent(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        updatedEvent: ContactEvent,
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        val today = Calendar.getInstance()

        val alarmDate = Calendar.getInstance(Locale.getDefault())
        alarmDate.set(Calendar.MONTH, updatedEvent.month)
        alarmDate.set(Calendar.DAY_OF_MONTH, updatedEvent.day)
        alarmDate.set(Calendar.YEAR, updatedEvent.year)

        updatedEvent.expired = today > alarmDate

        contactEventDao.updateContactEvent(
            updatedEvent.contact_event,
            updatedEvent.contact_event_reminder,
            updatedEvent.year,
            updatedEvent.month,
            updatedEvent.day,
            updatedEvent.ymd_format,
            updatedEvent.expired,
            updatedEvent.event_pk,
        )

        emit(DataState.data(
            response = Response(
                message = "Event Updated",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}