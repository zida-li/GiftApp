package dev.zidali.giftapp.business.interactors.main.fab

import android.util.Log
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEventEntity
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.main.fab.create_event.CreateEventState
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.util.*

class CreateEvent(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(
        contactEvent: ContactEvent
    ): Flow<DataState<CreateEventState>> = flow<DataState<CreateEventState>> {

        val today = Calendar.getInstance()

        val alarmDate = Calendar.getInstance(Locale.getDefault())
        alarmDate.set(Calendar.MONTH, contactEvent.month)
        alarmDate.set(Calendar.DAY_OF_MONTH, contactEvent.day)
        alarmDate.set(Calendar.YEAR, contactEvent.year)

        if (today > alarmDate) {
            contactEvent.expired = true
        }

        val pk = contactEventDao.insert(contactEvent.toContactEventEntity())

        emit(DataState.data(
            response = Response(
                message = "${contactEvent.contact_event} Event Created",
                uiComponentType = UIComponentType.Toast,
                messageType = MessageType.None,
            ),
            data = CreateEventState(
                new_event_pk_holder = pk.toInt()
            )
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}