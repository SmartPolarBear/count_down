package com.example.androiddevchallenge

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _hours = MutableLiveData(0)
    val hours: LiveData<Int> = _hours

    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int> = _minutes

    private val _seconds = MutableLiveData(0)
    val seconds: LiveData<Int> = _seconds

    private val _started = MutableLiveData(false)
    val started: LiveData<Boolean> = _started

    fun onHoursChange(newHours: Int) {
        _hours.value = newHours
    }

    fun onMinutesChange(newMinutes: Int) {
        _minutes.value = newMinutes
    }

    fun onSecondsChange(newSeconds: Int) {
        _seconds.value = newSeconds
    }

    fun toggleStartPause() {
        if (_started.value == true) {
            pause()
        } else {
            start()
        }
    }

    fun clear() {
        _started.value = false;

        _hours.value = 0
        _minutes.value = 0
        _seconds.value = 0
    }

    private fun start() {
        _started.value = true;
    }

    private fun pause() {
        _started.value = false;
    }
}