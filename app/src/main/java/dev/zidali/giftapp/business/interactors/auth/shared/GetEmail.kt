package dev.zidali.giftapp.business.interactors.auth.shared

import dev.zidali.giftapp.business.datasource.datastore.AppDataStore
import dev.zidali.giftapp.business.domain.models.Email
import dev.zidali.giftapp.business.domain.util.*
import dev.zidali.giftapp.presentation.util.DataStoreKeys
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class GetEmail(
    private val appDataStore: AppDataStore
) {

    fun execute(
        registerFragment: Boolean,
        loginFragment: Boolean,
    ): Flow<DataState<Email>> = flow {

        try {
            when {
                registerFragment -> {
                    val email = appDataStore.readValue(DataStoreKeys.REGISTRATION_EMAIL)
                    emit(
                        DataState.data(
                            response = null,
                            data = Email(
                                email = email!!
                            )
                        )
                    )
                }
                loginFragment -> {
                    val email = appDataStore.readValue(DataStoreKeys.LOGIN_EMAIL)
                    emit(
                        DataState.data(
                            response = null,
                            data = Email(
                                email = email!!
                            )
                        )
                    )
                }
                else -> {
                    emit(
                        DataState.error(
                            response = Response(
                                message = ErrorHandling.ERROR_NO_EMAIL_SAVED,
                                uiComponentType = UIComponentType.None,
                                messageType = MessageType.Error,
                            )
                        )
                    )
                }
            }
        } catch (e: Exception) {
            emit(
                DataState.error(
                    response = Response(
                        message = e.message,
                        uiComponentType = UIComponentType.None,
                        messageType = MessageType.Error,
                    )
                )
            )
        }
    }

}