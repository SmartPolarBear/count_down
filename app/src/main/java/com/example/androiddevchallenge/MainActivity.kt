/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.androiddevchallenge.ui.theme.CountDownTheme
import androidx.compose.runtime.livedata.observeAsState

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    companion object {
        fun setAlarm(context: Context, nowMilliseconds: Long, millisecondsRemaining: Long): Long {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)

            val deadline = nowMilliseconds + millisecondsRemaining
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, deadline, pendingIntent)
            return deadline
        }

        fun removeAlarm(context: Context) {
            val intent = Intent(context, TimerExpiredReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
        }

        val nowMilliseconds: Long
            @RequiresApi(Build.VERSION_CODES.N)
            get() = Calendar.getInstance().timeInMillis
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CountDownTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Count Down") },
                            elevation = 16.dp,
                        )
                    },
                    floatingActionButton = {
                        val started: Boolean by mainViewModel.started.observeAsState(false)

                        StartEndFloatingActionButton(started = started,
                            onClick = {
                                if (mainViewModel.toggleStartPause() == true) {
                                    setAlarm(
                                        this,
                                        nowMilliseconds,
                                        mainViewModel.parseHourMinuteSecondToMillisecond()
                                    )
                                } else {
                                    removeAlarm(this)
                                }
                            })
                    }
                )
                {
                    Surface(color = MaterialTheme.colors.background) {
                        Main(mainViewModel = mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NumericUpDown(
    value: Long = 0,
    range: LongRange = LongRange(0, 60),
    onValueChange: (Long) -> Unit
) {

    val updateValue = { newValue: Long ->
        onValueChange(
            when {
                range.contains(newValue) -> newValue
                newValue < range.first -> range.first
                newValue > range.last -> range.last
                else -> 0
            }
        )
    }

    val updateValueWithText = { str: String ->
        if (str.isNotEmpty() && str.isDigitsOnly()) {
            updateValue(str.toLong())
        } else {
            updateValue(0)
        }
    }

    Column(
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        OutlinedButton(onClick = { updateValue(value + 1) }) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = stringResource(R.string.up)
            )
        }

        Box(
            modifier = Modifier
                .padding(bottom = 4.dp)
        ) {
            OutlinedTextField(
                value = value.toString(),
                onValueChange = { updateValueWithText(it) },
                singleLine = true,
                modifier = Modifier
                    .requiredWidth(52.dp)
            )
        }

        OutlinedButton(onClick = { updateValue(value - 1) }) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = stringResource(R.string.down)
            )
        }
    }
}

@Composable
fun TimeUnitText(
    text: String
) {
    Text(
        text = text,
    )
}

// Start building your app here!
@Composable
fun Main(mainViewModel: MainViewModel) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        )
        {
            val hours by mainViewModel.hours.observeAsState(0L)
            NumericUpDown(
                value = hours, range = 0..23L
            ) { mainViewModel.onHoursChange(it) }
            TimeUnitText(text = "h")

            val minutes by mainViewModel.minutes.observeAsState(0L)
            NumericUpDown(
                value = minutes, range = 0..60L
            ) { mainViewModel.onMinutesChange(it) }
            TimeUnitText(text = "m")

            val seconds by mainViewModel.seconds.observeAsState(0L)
            NumericUpDown(
                value = seconds, range = 0..60L
            ) { mainViewModel.onSecondsChange(it) }
            TimeUnitText(text = "s")
        }
    }


}


@Composable
fun StartEndFloatingActionButton(
    started: Boolean,
    onClick: () -> Unit
) {
    FloatingActionButton(onClick = onClick) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = when {
                    started -> Icons.Default.Pause
                    else -> Icons.Default.PlayArrow
                },
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(start = 8.dp, top = 3.dp),
                text = when {
                    started -> stringResource(id = R.string.end)
                    else -> stringResource(id = R.string.start)
                }
            )
        }
    }
}


@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    val mainViewModel = MainViewModel()
    CountDownTheme {
        Main(mainViewModel)
    }
}

@Preview("Dark Theme", widthDp = 360, heightDp = 640)
@Composable
fun DarkPreview() {
    val mainViewModel = MainViewModel()
    CountDownTheme(darkTheme = true) {
        Main(mainViewModel)
    }
}
