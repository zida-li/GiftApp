package dev.zidali.giftapp.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class AlarmReceiver(
    private val contactEventDao: ContactEventDao,
): BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        if (context != null && intent != null && intent.action != null) {
//            if (intent.action!!.equals(context.getString(R.string.action_notify_gift_event), ignoreCase = true)) {
//                if (intent.extras != null) {
//                    val reminderData = intent.extras!!.getString(Constants.KEY_ID)
//                    CoroutineScope(IO).launch {
//                        contactEventDao.searchByEvent()
//                    }
//                    if (reminderData != null) {
//                        NotificationHelper.createNotificationForContact(context, reminderData)
//                    }
//                }
//            }
//        }
    }


}