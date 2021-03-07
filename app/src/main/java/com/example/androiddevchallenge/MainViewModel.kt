package com.example.androiddevchallenge

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

    fun onHoursChange(newHours: Int) {
        _hours.value = newHours
    }

    fun onMinutesChange(newMinutes: Int) {
        _minutes.value = newMinutes
    }

    fun onSecondsChange(newSeconds: Int) {
        _seconds.value = newSeconds
    }

}