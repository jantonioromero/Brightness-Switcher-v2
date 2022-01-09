package com.arreis.brightnessswitcher

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository

class MainViewModel(
    val brightnessLevelRepository: BrightnessLevelRepository
    ) : ViewModel() {

    private val _brightnessLevels = MutableLiveData<List<BrightnessLevel>>()
    val brightnessLevels: LiveData<List<BrightnessLevel>> = _brightnessLevels

}
