package com.example.androiddevchallenge.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.androiddevchallenge.AppConstants
import com.example.androiddevchallenge.R
import com.example.androiddevchallenge.TimerNotificationActionReceiver


class NotificationUtil {
    companion object {
        private const val CHANNEL_ID_TIMER = "menu_timer"
        private const val CHANNEL_NAME_TIMER = "Countdown Timer"
        private const val TIMER_ID = 0

        fun showTimerExpired(context: Context) {
            val stopIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            stopIntent.action = AppConstants.ACTION_STOP
            val stopPendingIntent = PendingIntent.getBroadcast(
                context,
                0, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val add1minIntent = Intent(context, TimerNotificationActionReceiver::class.java)
            add1minIntent.action = AppConstants.ACTION_STOP
            val add1minPendingIntent = PendingIntent.getBroadcast(
                context,
                0, add1minIntent, PendingIntent.FLAG_UPDATE_CURRENT
            )

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val notificationChannel = NotificationChannel(
                    CHANNEL_ID_TIMER,
                    CHANNEL_NAME_TIMER,
                    NotificationManager.IMPORTANCE_MAX
                )
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.RED
                notificationChannel.enableVibration(true)
                notificationChannel.vibrationPattern = longArrayOf(
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000
                )
                notificationChannel.description =
                    context.getString(R.string.stopwatch_notification_channel_description)

                notificationManager.createNotificationChannel(notificationChannel)
            }

            val notificationBuilder =
                NotificationCompat.Builder(context, CHANNEL_ID_TIMER)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.alarm))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_STOPWATCH)
                    .setVibrate(longArrayOf(0, 1000, 1000, 1000, 1000, 1000, 1000, 1000))
                    .setChannelId(CHANNEL_ID_TIMER)
                    .addAction(
                        R.drawable.ic_add,
                        context.getString(R.string.add1min),
                        stopPendingIntent
                    )
                    .addAction(
                        R.drawable.ic_stop,
                        context.getString(R.string.stop),
                        add1minPendingIntent
                    )

            notificationManager.notify(TIMER_ID, notificationBuilder.build())

        }


    }
}