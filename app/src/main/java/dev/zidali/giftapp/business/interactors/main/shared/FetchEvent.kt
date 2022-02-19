package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchEvent(
    private val contactEventDao: ContactEventDao
) {

    fun execute(
        contact_pk: Int,
        event_pk: Int,
    ): Flow<DataState<ContactEvent>> = flow<DataState<ContactEvent>> {

        val result = contactEventDao.searchByEvent(contact_pk, event_pk)?.toContactEvent()

        emit(DataState.data(
            response = null,
            data = result
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}