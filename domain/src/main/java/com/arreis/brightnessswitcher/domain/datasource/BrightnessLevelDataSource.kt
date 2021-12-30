package com.arreis.brightnessswitcher.domain.datasource

import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel

interface BrightnessLevelDataSource {
    fun resetBrightnessLevels()
    fun brightnessLevels(): List<BrightnessLevel>?
    fun saveBrightnessLevels(levels: List<BrightnessLevel>)
}