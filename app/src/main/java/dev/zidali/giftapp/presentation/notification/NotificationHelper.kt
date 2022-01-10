package dev.zidali.giftapp.presentation.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.zidali.giftapp.R
import dev.zidali.giftapp.business.domain.models.ContactEvent
import dev.zidali.giftapp.presentation.main.MainActivity

object NotificationHelper {

    fun createNotificationChannel(context: Context, importance: Int, showBadge: Boolean, name: String, description: String) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channelId = "${context.packageName}-$name"
            val channel = NotificationChannel(channelId, name, importance)
            channel.description = description
            channel.setShowBadge(showBadge)

            // Register the channel with the system
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun createNotification(context: Context, title: String, message: String,
                           bigText: String, autoCancel: Boolean) {

        val channelId = "${context.packageName}-${context.getString(R.string.app_name)}"
        val notificationBuilder = NotificationCompat.Builder(context, channelId).apply {
            setSmallIcon(R.drawable.ic_gift)
            setContentTitle(title)
            setContentText(message)
            setAutoCancel(autoCancel)
            setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            priority = NotificationCompat.PRIORITY_DEFAULT
            setAutoCancel(autoCancel)

            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
            setContentIntent(pendingIntent)
        }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, notificationBuilder.build())
    }

//    fun createNotificationForContact(context: Context, reminderData: ContactEvent) {
//
//        // create a group notification
//        val groupBuilder = buildGroupNotification(context, reminderData)
//
//        // create the pet notification
//        val notificationBuilder = buildNotificationForContact(context, reminderData)
//
//        // add an action to the pet notification
//        val administerPendingIntent = createPendingIntentForAction(context, reminderData)
//        notificationBuilder.addAction(R.drawable.baseline_done_black_24, context.getString(R.string.administer), administerPendingIntent)
//
//        // call notify for both the group and the pet notification
//        val notificationManager = NotificationManagerCompat.from(context)
//        notificationManager.notify(reminderData.type.ordinal, groupBuilder.build())
//        notificationManager.notify(reminderData.id, notificationBuilder.build())
//    }
//
//    private fun buildGroupNotification(context: Context, reminderData: ReminderData): NotificationCompat.Builder {
//        val channelId = "${context.packageName}-${reminderData.type.name}"
//        return NotificationCompat.Builder(context, channelId).apply {
//            setSmallIcon(R.drawable.ic_gift)
//            setContentTitle(reminderData.type.name)
//            setContentText(context.getString(R.string.group_notification_for, reminderData.type.name))
//            setStyle(NotificationCompat.BigTextStyle().bigText(context.getString(R.string.group_notification_for, reminderData.type.name)))
//            setAutoCancel(true)
//            setGroupSummary(true)
//            setGroup(reminderData.type.name)
//        }
//    }
//
//    private fun buildNotificationForContact(context: Context, reminderData: ReminderData): NotificationCompat.Builder {
//
//
//        val channelId = "${context.packageName}-${reminderData.type.name}"
//
//        return NotificationCompat.Builder(context, channelId).apply {
//            setSmallIcon(R.drawable.ic_stat_medicine)
//            setContentTitle(reminderData.name)
//            setAutoCancel(true)
//
//            // get a drawable reference for the LargeIcon
//            val drawable = when (reminderData.type) {
//                ReminderData.PetType.Dog -> R.drawable.dog
//                ReminderData.PetType.Cat -> R.drawable.cat
//                else -> R.drawable.other
//            }
//            setLargeIcon(BitmapFactory.decodeResource(context.resources, drawable))
//            setContentText("${reminderData.medicine}, ${reminderData.desc}")
//            setGroup(reminderData.type.name)
//
//            // note is not important so if it doesn't exist no big deal
//            if (reminderData.note != null) {
//                setStyle(NotificationCompat.BigTextStyle().bigText(reminderData.note))
//            }
//
//            // Launches the app to open the reminder edit screen when tapping the whole notification
//            val intent = Intent(context, MainActivity::class.java).apply {
//                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                putExtra(ReminderDialog.KEY_ID, reminderData.id)
//            }
//
//            val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
//            setContentIntent(pendingIntent)
//        }
//    }

}