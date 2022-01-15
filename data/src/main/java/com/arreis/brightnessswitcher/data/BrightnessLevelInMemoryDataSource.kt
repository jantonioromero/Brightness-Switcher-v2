package com.arreis.brightnessswitcher.data

import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel

class BrightnessLevelInMemoryDataSource(
    levels: List<BrightnessLevel> = emptyList()
): BrightnessLevelDataSource {

    private val brightnessLevels = levels.toMutableList()

    override fun resetBrightnessLevels() {
        brightnessLevels.clear()
    }

    override fun brightnessLevels(): List<BrightnessLevel>? {
        return brightnessLevels
    }

    override fun saveBrightnessLevels(levels: List<BrightnessLevel>) {
        brightnessLevels.clear()
        brightnessLevels.addAll(levels)
    }
}
