package dev.zidali.giftapp.presentation.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.datasource.cache.contacts.ContactEventDao
import dev.zidali.giftapp.business.datasource.cache.contacts.toContactEvent
import dev.zidali.giftapp.util.Constants.Companion.TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver: BroadcastReceiver() {

    @Inject
    lateinit var contactEventDao: ContactEventDao

    override fun onReceive(context: Context?, intent: Intent?) {
//        Log.d(Constants.TAG, "AlarmReceiver()")
        if (context != null && intent != null && intent.action != null) {
            if (intent.action!!.equals(context.getString(R.string.action_notify_gift_event), ignoreCase = true)) {
                if (intent.extras != null) {
                    val contactPk = intent.extras!!.getInt("CONTACT_PK")
                    val eventPk = intent.extras!!.getInt("EVENT_PK")
                    if (contactPk != null && eventPk != null) {
                        CoroutineScope(IO).launch {
                            val event = contactEventDao.searchByEvent(contactPk, eventPk)?.toContactEvent()
//                            Log.d(TAG, "AlarmReceiver: $event")
                            try {
                                NotificationHelper.createNotificationForContact(context, event!!)
                            } catch (e: Exception){
                                Log.d(TAG, e.toString())
                            }
                        }
                    }
                }
            }
        }
    }

}