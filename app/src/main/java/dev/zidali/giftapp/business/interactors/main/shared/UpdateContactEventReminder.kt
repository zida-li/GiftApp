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


class UpdateContactEventReminder(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        contactEventDao.updateContactReminder(
            contactEvent.contact_event_reminder, contactEvent.pk, contactEvent.contact_event
        )

        emit(
            DataState.data(
                response = Response(
                    message = "Notification Updated",
                    uiComponentType = UIComponentType.Toast,
                    messageType = MessageType.None
                )
            )
        )

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}