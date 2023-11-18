package com.example.dailyfootprint.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    private val _someLiveData = MutableLiveData<String>()
    val someLiveData: LiveData<String>
        get() = _someLiveData
    init {
        // 데이터 초기화 또는 가져오기 로직
    }
    fun updateLiveData(newValue: String) {
        _someLiveData.value = newValue
    }
}
