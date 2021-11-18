package dev.zidali.giftapp.business.interactors.session

import android.util.Log
import dev.zidali.giftapp.business.datasource.cache.account.AccountPropertiesDao
import dev.zidali.giftapp.business.datasource.cache.auth.AuthTokenDao
import dev.zidali.giftapp.business.datasource.cache.auth.toAuthToken
import dev.zidali.giftapp.business.domain.models.AuthToken
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.business.domain.util.ErrorHandling.Companion.ERROR_NO_PREVIOUS_AUTH_USER
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

/**
 * Attempt to authenticate as soon as the user launches the app.
 * If no user was authenticated in a previous session then do nothing.
 */
class CheckPreviousAuthUser(
    private val accountPropertiesDao: AccountPropertiesDao,
    private val authTokenDao: AuthTokenDao,
) {
    fun execute(
        email: String,
    ): Flow<DataState<AuthToken>> = flow {
        emit(DataState.loading<AuthToken>())
        var authToken: AuthToken? = null
        val entity = accountPropertiesDao.searchByEmail(email)
        Log.d(TAG, "CheckPreviousAuthUser: Email: ${email}")
        if(entity != null){
            authToken = authTokenDao.searchByEmail(entity.email)?.toAuthToken()
            if(authToken != null){
                emit(DataState.data(response = null, data = authToken))
            }
        }
        if(authToken == null){
            throw Exception(ERROR_NO_PREVIOUS_AUTH_USER)
        }
    }.catch{ e ->
        e.printStackTrace()
        emit(returnNoPreviousAuthUser())
    }

    /**
     * If no user was previously authenticated then emit this error. The UI is waiting for it.
     */
    private fun returnNoPreviousAuthUser(): DataState<AuthToken> {
        return DataState.error<AuthToken>(
            response = Response(
                SuccessHandling.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                UIComponentType.None,
                MessageType.Error
            )
        )
    }
}