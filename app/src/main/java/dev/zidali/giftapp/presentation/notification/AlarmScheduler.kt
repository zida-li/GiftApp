package dev.zidali.giftapp.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.util.Constants
import java.util.*
import java.util.Calendar.*

object AlarmScheduler {

//    fun scheduleAlarmsForReminder(context: Context, reminderData: ContactEvent) {
//
//        // get the AlarmManager reference
//        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//
//        // Schedule the alarm based on user preference
//
//
//            // get the PendingIntent for the alarm
//            val alarmIntent = createPendingIntent(context, reminderData, reminderData.contact_event)
//
//            // schedule the alarm
//            scheduleAlarm(reminderData, dayOfWeek, alarmIntent, alarmMgr)
//        }
//    }
//
//    private fun scheduleAlarm(reminderData: ContactEvent, month: Int, dayOfMonth: Int, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {
//
//        // Set up the time to schedule the alarm
//        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
//        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
//        datetimeToAlarm.set(MONTH, month)
//        datetimeToAlarm.set(DAY_OF_MONTH, dayOfMonth)
//
//        // Compare the datetimeToAlarm to today
//        val today = Calendar.getInstance(Locale.getDefault())
//        if (shouldNotifyToday(dayOfMonth, today, datetimeToAlarm)) {
//
//            // schedule for today
//            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
//                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
//            return
//        }
//
//        // schedule 1 week out from the day
//        datetimeToAlarm.roll(Calendar.WEEK_OF_YEAR, 1)
//        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
//            datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
//    }
//
//    private fun shouldNotifyToday(dayOfWeek: Int, today: Calendar, datetimeToAlarm: Calendar): Boolean {
//        return dayOfWeek == today.get(Calendar.DAY_OF_WEEK) &&
//                today.get(Calendar.HOUR_OF_DAY) <= datetimeToAlarm.get(Calendar.HOUR_OF_DAY) &&
//                today.get(Calendar.MINUTE) <= datetimeToAlarm.get(Calendar.MINUTE)
//    }
//
//    private fun createPendingIntent(context: Context, reminderData: ContactEvent, event: String?): PendingIntent? {
//        // create the intent using a unique type
//        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
//            action = context.getString(R.string.action_notify_gift_event)
//            type = "$event-${reminderData.contact_event}"
//            putExtra(Constants.KEY_ID, reminderData.contact_event)
//        }
//
//        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//    }

}