package dev.zidali.giftapp.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.business.domain.util.Converters
import dev.zidali.giftapp.util.Constants
import java.time.LocalDate
import java.util.*
import java.util.Calendar.*

object AlarmScheduler {

    @RequiresApi(Build.VERSION_CODES.O)
    fun scheduleInitialAlarmsForReminder(context: Context, reminderData: ContactEvent) {

        // get the AlarmManager reference
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Set up the time to schedule the alarm
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(MONTH, reminderData.month)
        datetimeToAlarm.set(DAY_OF_MONTH, reminderData.day)
        datetimeToAlarm.set(HOUR_OF_DAY, 0)
        datetimeToAlarm.set(MINUTE, 0)

        //        Log.d(Constants.TAG, "scheduleAlarm")

        val localDate = LocalDate.of(
            reminderData.year,
            Converters.convertCalendarIntMonthToIntMonth(reminderData.month),
            reminderData.day,
        )

        // Compare the datetimeToAlarm to today
        val today = Calendar.getInstance(Locale.getDefault())
        if (shouldNotifyToday(reminderData.day, today)) {

            // get the PendingIntent for the alarm
            val alarmIntent = createPendingIntent(context, reminderData, "dayOf")

//            Log.d(Constants.TAG, "shouldNotifyToday = true")

            // schedule for today
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
        }

        // Schedule the alarm based on user preference

        if(reminderData.contact_event_reminder.contains("day", ignoreCase = true)){
            Log.d(Constants.TAG, "schedule 1 day out")

            val alarmIntent = createPendingIntent(context, reminderData, "day")

            val modifiedLocalDate = localDate.minusDays(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, alarmIntent)

            Log.d(Constants.TAG, "schedule 1 day out: $datetimeToAlarm")
        }
        if(reminderData.contact_event_reminder.contains("week", ignoreCase = true)){
            Log.d(Constants.TAG, "schedule 1 week out")

            val alarmIntent = createPendingIntent(context, reminderData, "week")

            val modifiedLocalDate = localDate.minusWeeks(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, alarmIntent)

            Log.d(Constants.TAG, "schedule 1 week out: $datetimeToAlarm")
        }
        if(reminderData.contact_event_reminder.contains("month", ignoreCase = true)){
            Log.d(Constants.TAG, "schedule 1 month out")

            val alarmIntent = createPendingIntent(context, reminderData, "month")

            val modifiedLocalDate = localDate.minusMonths(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)

            Log.d(Constants.TAG, "schedule 1 month out: $datetimeToAlarm")
        }

        Log.d(Constants.TAG, "scheduleAlarmForReminder: ${reminderData.contact_name}")

        }

    fun cancelScheduledAlarmForReminder(context: Context, reminderData: ContactEvent, reminder: String) {

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_gift_event)
            type = "${reminder}-${reminderData.contact_event}"
            putExtra(Constants.KEY_ID, reminderData.contact_event)
        }

        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        Log.d(Constants.TAG, "cancelScheduledAlarmForReminder: ${reminderData.contact_name} + $reminder")

        if (pendingIntent != null) {
            alarmMgr.cancel(pendingIntent)
        }
    }

    private fun shouldNotifyToday(dayOfMonth: Int, today: Calendar): Boolean {
//        Log.d(Constants.TAG, "${dayOfMonth} ${today.get(DAY_OF_MONTH)}")
        return dayOfMonth == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun createPendingIntent(context: Context, reminderData: ContactEvent, reminder: String): PendingIntent? {
//        Log.d(Constants.TAG, "createPendingIntent")
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_gift_event)
            type = "${reminder}-${reminderData.contact_event}"
            val intentBundle = Bundle()
            intentBundle.putString("CONTACT_NAME", reminderData.contact_name)
            intentBundle.putString("CONTACT_EVENT", reminderData.contact_event)
            putExtras(intentBundle)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)


    }

}