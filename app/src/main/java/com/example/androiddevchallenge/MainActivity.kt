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

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.example.androiddevchallenge.ui.theme.CountDownTheme
import com.example.androiddevchallenge.util.NotificationUtil

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CountDownTheme {
                val started: Boolean by mainViewModel.started.observeAsState(false)

                val background by animateColorAsState(
                    targetValue = if (started) MaterialTheme.colors.secondary
                    else MaterialTheme.colors.secondaryVariant,
                    animationSpec = tween(durationMillis = 500)
                )

                Scaffold(
                    modifier = Modifier.background(background),
                    topBar = {
                        TopAppBar(
                            title = { Text(text = "Count Down") },
                            elevation = 16.dp,
                        )
                    },
                    floatingActionButton = {

                        StartEndFloatingActionButton(
                            started = started
                        ) {
                            mainViewModel.toggleStartPause {
                                NotificationUtil.showTimerExpired(this)
                            }
                        }
                    }
                ) {
                    Surface() {
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
        ) {
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
