package com.example.zhdun

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WaitingViewModel : ViewModel() {
    private val _minutes = MutableLiveData(0)
    val minutes: LiveData<Int> = _minutes

    private val _isLowBattery = MutableLiveData(false)
    val isLowBattery: LiveData<Boolean> = _isLowBattery

    fun incrementTime() {
        _minutes.value = (_minutes.value ?: 0) + 1
    }

    fun setLowBattery(low: Boolean) {
        _isLowBattery.value = low
    }

    fun reset() {
        _minutes.value = 0
        _isLowBattery.value = false
    }
}
