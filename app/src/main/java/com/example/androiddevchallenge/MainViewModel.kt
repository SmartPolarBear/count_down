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

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _hours = MutableLiveData(0L)
    val hours: LiveData<Long> = _hours

    private val _minutes = MutableLiveData(0L)
    val minutes: LiveData<Long> = _minutes

    private val _seconds = MutableLiveData(0L)
    val seconds: LiveData<Long> = _seconds

    private val _started = MutableLiveData(false)
    val started: LiveData<Boolean> = _started

    private lateinit var _countDownTimer: CountDownTimer
    private var _millisecondsLeft: Long = 0L

    fun onHoursChange(newHours: Long) {
        _hours.value = newHours
    }

    fun onMinutesChange(newMinutes: Long) {
        _minutes.value = newMinutes
    }

    fun onSecondsChange(newSeconds: Long) {
        _seconds.value = newSeconds
    }

    fun toggleStartPause(onTimerFinished: () -> Unit): Boolean? {
        if (_started.value == true) {
            pause()
        } else {
            start(onTimerFinished)
        }

        return started.value
    }

    fun clear() {
        _started.value = false

        _hours.value = 0
        _minutes.value = 0
        _seconds.value = 0
    }

    private fun parseHourMinuteSecondToMillisecond(): Long {
        return 1000L * (
            (_hours.value?.times(3600L) ?: 0) +
                (_minutes.value?.times(60L) ?: 0) +
                _seconds.value!!
            )
    }

    private fun extractHours(milliseconds: Long): Long {
        return (milliseconds / 1000) / 3600
    }

    private fun extractMinutes(milliseconds: Long): Long {
        return ((milliseconds / 1000) % 3600) / 60
    }

    private fun extractSeconds(milliseconds: Long): Long {
        return ((milliseconds / 1000) % 3600) % 60
    }

    private fun start(onTimerFinished: () -> Unit) {
        _millisecondsLeft = parseHourMinuteSecondToMillisecond()

        _countDownTimer = object : CountDownTimer(_millisecondsLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onHoursChange(extractHours(millisUntilFinished))
                onMinutesChange(extractMinutes(millisUntilFinished))
                onSecondsChange(extractSeconds(millisUntilFinished))
            }

            override fun onFinish() {
                _started.value = false
                onTimerFinished()
                clear()
            }
        }

        _countDownTimer.start()

        _started.value = true
    }

    private fun pause() {
        _countDownTimer.cancel()

        _started.value = false
    }
}
