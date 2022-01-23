package dev.zidali.giftapp.presentation.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
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
    fun scheduleAlarmsForReminder(context: Context, reminderData: ContactEvent) {

        // get the AlarmManager reference
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Schedule the alarm based on user preference

            // get the PendingIntent for the alarm
            val alarmIntent = createPendingIntent(context, reminderData, reminderData.contact_event)

            // schedule the alarm
            scheduleAlarm(reminderData, alarmIntent, alarmMgr)

        Log.d(Constants.TAG, "scheduleAlarmForReminder: ${reminderData.contact_event_reminder}")
        }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun scheduleAlarm(reminderData: ContactEvent, alarmIntent: PendingIntent?, alarmMgr: AlarmManager) {

//        Log.d(Constants.TAG, "scheduleAlarm")

        // Set up the time to schedule the alarm
        val datetimeToAlarm = Calendar.getInstance(Locale.getDefault())
        datetimeToAlarm.timeInMillis = System.currentTimeMillis()
        datetimeToAlarm.set(MONTH, reminderData.month)
        datetimeToAlarm.set(DAY_OF_MONTH, reminderData.day)
        datetimeToAlarm.set(HOUR_OF_DAY, 0)
        datetimeToAlarm.set(MINUTE, 0)

        val localDate = LocalDate.of(
            reminderData.year,
            Converters.convertCalendarIntMonthToIntMonth(reminderData.month),
            reminderData.day,
        )

        // Compare the datetimeToAlarm to today
        val today = Calendar.getInstance(Locale.getDefault())
        if (shouldNotifyToday(reminderData.day, today)) {

//            Log.d(Constants.TAG, "shouldNotifyToday = true")

            // schedule for today
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)
        }

        if(reminderData.contact_event_reminder.contains("day", ignoreCase = true)) {
            // schedule 1 week out from the day
            Log.d(Constants.TAG, "schedule 1 day out")

            val modifiedLocalDate = localDate.minusDays(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, alarmIntent)

            Log.d(Constants.TAG, "schedule 1 day out: $datetimeToAlarm")
        }

        if(reminderData.contact_event_reminder.contains("week", ignoreCase = true)) {
            // schedule 1 week out from the day
            Log.d(Constants.TAG, "schedule 1 week out")

            val modifiedLocalDate = localDate.minusWeeks(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.set(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, alarmIntent)

            Log.d(Constants.TAG, "schedule 1 week out: $datetimeToAlarm")
        }

        if(reminderData.contact_event_reminder.contains("month", ignoreCase = true)) {
            // schedule 1 week out from the day
            Log.d(Constants.TAG, "schedule 1 month out")

            val modifiedLocalDate = localDate.minusMonths(1)

            datetimeToAlarm.set(YEAR,  modifiedLocalDate.year)
            datetimeToAlarm.set(MONTH, Converters.convertIntMonthToCalendarIntMonth(modifiedLocalDate.monthValue))
            datetimeToAlarm.set(DATE, modifiedLocalDate.dayOfMonth)

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP,
                datetimeToAlarm.timeInMillis, (1000 * 60 * 60 * 24 * 7).toLong(), alarmIntent)

            Log.d(Constants.TAG, "schedule 1 month out: $datetimeToAlarm")
        }

    }

    private fun shouldNotifyToday(dayOfMonth: Int, today: Calendar): Boolean {
//        Log.d(Constants.TAG, "${dayOfMonth} ${today.get(DAY_OF_MONTH)}")
        return dayOfMonth == today.get(Calendar.DAY_OF_MONTH)
    }

    private fun createPendingIntent(context: Context, reminderData: ContactEvent, event: String?): PendingIntent? {
//        Log.d(Constants.TAG, "createPendingIntent")
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
            action = context.getString(R.string.action_notify_gift_event)
            type = "$event-${reminderData.contact_event}"
            putExtra(Constants.KEY_ID, reminderData.contact_event)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

}