package com.arreis.brightnessswitcher.datamodel

import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel

class BrightnessLevelRepository(
    private val dataSource: BrightnessLevelDataSource
    ) {

    fun getBrightnessLevels(): List<BrightnessLevel> {
        return dataSource.brightnessLevels() ?: defaultLevels()
    }

    fun saveBrightnessLevels(levels: List<BrightnessLevel>) {
        dataSource.saveBrightnessLevels(levels)
    }

    private fun defaultLevels() : List<BrightnessLevel> {
        return listOf(
            BrightnessLevel.FixedValue(50.0 / 100),
            BrightnessLevel.FixedValue(1.0 / 100))
    }

    companion object {
        const val MIN_BRIGHTNESS_LEVELS = 2
        const val MAX_BRIGHTNESS_LEVELS = 10
        const val BRIGHTNESS_LEVEL_DEFAULT = 0.5
        const val BRIGHTNESS_LEVEL_AUTO = -1.0
    }
}