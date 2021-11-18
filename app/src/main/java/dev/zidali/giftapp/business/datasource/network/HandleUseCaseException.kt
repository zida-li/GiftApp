package dev.zidali.giftapp.business.datasource.network

import android.util.Log
import dev.zidali.giftapp.business.domain.util.DataState
import dev.zidali.giftapp.business.domain.util.MessageType
import dev.zidali.giftapp.business.domain.util.Response
import dev.zidali.giftapp.business.domain.util.UIComponentType
import dev.zidali.giftapp.util.Constants

fun <T> handleUseCaseException(e: Throwable): DataState<T> {
    e.printStackTrace()
    Log.d(Constants.TAG, e.message!!)
    return DataState.error<T>(
        response = Response(
            message = e.message,
            uiComponentType = UIComponentType.Dialog,
            messageType = MessageType.Error
        )
    )
}
