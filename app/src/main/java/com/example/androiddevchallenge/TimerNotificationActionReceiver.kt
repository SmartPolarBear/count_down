package com.example.androiddevchallenge

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class TimerNotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            AppConstants.ACTION_STOP -> {

            }
            AppConstants.ACTION_ADD1MIN -> {

            }
            else -> {

            }
        }
    }
}
