package dev.zidali.giftapp.business.interactors.main.shared

import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.contacts.contact_detail.event.EventState
import dev.zidali.giftapp.presentation.main.home.AllEventState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class FetchAllEvents(
    private val contactEventDao: ContactEventDao,
) {

    fun execute(): Flow<DataState<AllEventState>> = flow {

        val results = contactEventDao.getAllContactEvents().map { it.toContactEvent() }.toMutableList()

        val events = AllEventState(
            contact_events = results
        )

        emit(DataState.data(
            response = null,
            data = events
        ))

    }.catch { e->
        emit(handleUseCaseException(e))
    }

}