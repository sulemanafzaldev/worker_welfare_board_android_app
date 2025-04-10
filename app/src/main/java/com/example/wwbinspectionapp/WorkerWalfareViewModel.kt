// WorkerWalfareViewModel.kt
package com.example.wwbinspectionapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkerWalfareViewModel @Inject constructor() : ViewModel() {
    private val _factoryId = MutableLiveData<Int>()
    val factoryId: LiveData<Int> get() = _factoryId

    fun setFactoryId(id: Int) {
        _factoryId.value = id
    }
}
