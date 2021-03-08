package com.example.androiddevchallenge

import android.os.CountDownTimer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var milliseconds by mutableStateOf(0L)
        private set

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


    fun toggleStartPause() : Boolean? {
        if (_started.value == true) {
            pause()
        } else {
            start()
        }

        return started.value
    }

    fun clear() {
        _started.value = false;

        _hours.value = 0
        _minutes.value = 0
        _seconds.value = 0
    }

    fun parseHourMinuteSecondToMillisecond(): Long {
        return 1000L * ((_hours.value?.times(3600L) ?: 0) +
                (_minutes.value?.times(60L) ?: 0) +
                _seconds.value!!)
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

    private fun start() {
        _millisecondsLeft = parseHourMinuteSecondToMillisecond()

        _countDownTimer = object : CountDownTimer(_millisecondsLeft, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                onHoursChange(extractHours(millisUntilFinished))
                onMinutesChange(extractMinutes(millisUntilFinished))
                onSecondsChange(extractSeconds(millisUntilFinished))
            }

            override fun onFinish() {
                _started.value = false

                clear()
            }
        }

        _countDownTimer.start();

        _started.value = true;
    }

    private fun pause() {
        _countDownTimer.cancel()

        _started.value = false;
    }
}