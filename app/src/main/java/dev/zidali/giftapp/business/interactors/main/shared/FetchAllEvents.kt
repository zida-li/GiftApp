package dev.zidali.giftapp.business.interactors.main.shared

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventEntity
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.business.datasource.network.handleUseCaseException
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.presentation.main.all_events.AllEventState
import dev.zidali.giftapp.util.Constants
import dev.zidali.giftapp.util.cLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.*

class FetchAllEvents(
    private val contactEventDao: ContactEventDao,
    private val firebaseAuth: FirebaseAuth,
    private val fireStore: FirebaseFirestore,
) {

    fun execute(): Flow<DataState<AllEventState>> = flow {

        val owner = firebaseAuth.currentUser!!.email!!

        val results = contactEventDao.getAllOwnerEvents(
            owner
        ).map { it.toContactEvent() }.toMutableList()

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