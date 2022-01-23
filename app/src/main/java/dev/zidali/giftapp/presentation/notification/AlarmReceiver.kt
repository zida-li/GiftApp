package dev.zidali.giftapp.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.constraintlayout.widget.ConstraintAttribute
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var contactEventDao: ContactEventDao

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(Constants.TAG, "AlarmReceiver()")
        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(context.getString(R.string.action_notify_gift_event), ignoreCase = true)) {
                if (intent.extras != null) {
                    val reminderData = intent.extras!!.getString(Constants.KEY_ID)
                    if (reminderData != null) {
                        CoroutineScope(IO).launch {
                            val event = contactEventDao.searchByEvent(reminderData)?.toContactEvent()
                            NotificationHelper.createNotificationForContact(context, event!!)
                        }
                    }
                }
            }
        }
    }

}