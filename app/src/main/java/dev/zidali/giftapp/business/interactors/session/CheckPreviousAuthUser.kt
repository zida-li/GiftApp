package dev.zidali.giftapp.business.interactors.session

import com.google.firebase.auth.FirebaseAuth
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.account.toAccountProperties
import dev.zidali.giftapp.business.datasource.cache.account.toEntity
import dev.zidali.giftapp.business.domain.models.AccountProperties
import dev.zidali.giftapp.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.lang.Exception

/**
 * Attempt to authenticate as soon as the user launches the app.
 * If no user was authenticated in a previous session then do nothing.
 */
class CheckPreviousAuthUser(
    private val firebaseAuth: FirebaseAuth,
    private val accountPropertiesDao: AccountPropertiesDao,
) {
    fun execute(): Flow<DataState<AccountProperties>> = flow {

        emit(DataState.loading<AccountProperties>())

        if(firebaseAuth.currentUser != null) {

            emit(
                DataState.data(
                    response = null,
                    data = AccountProperties(
                        current_authUser_email = firebaseAuth.currentUser?.email!!
                    )
                )
            )
        } else {
            throw Exception(ErrorHandling.ERROR_NO_PREVIOUS_AUTH_USER)
        }


    }.catch{ e ->
        e.printStackTrace()
        emit(returnNoPreviousAuthUser())
    }

    /**
     * If no user was previously authenticated then emit this error. The UI is waiting for it.
     */
    private fun returnNoPreviousAuthUser(): DataState<AccountProperties> {
        return DataState.error<AccountProperties>(
            response = Response(
                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None,
                MessageType.Error,
            )
        )
    }
}