package com.example.androiddevchallenge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.androiddevchallenge.util.NotificationUtil

class TimerExpiredReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)
    }
}