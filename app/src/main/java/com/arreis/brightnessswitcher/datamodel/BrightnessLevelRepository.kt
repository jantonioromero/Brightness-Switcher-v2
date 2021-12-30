package com.arreis.brightnessswitcher.datamodel

import android.content.Context
import java.util.Vector

class BrightnessLevelRepository(
    private val context: Context
    ) {

    val dataSource: BrightnessLevelDataSource = BrightnessLevelFileDataSource(context)

    fun resetBrightnessLevels() {
        context.deleteFile(BRIGHTNESS_LEVELS_FILENAME)
    }

    fun getBrightnessLevels(): Vector<Double> {
        return dataSource.brightnessLevels() ?: defaultLevels()
    }

    fun saveBrightnessLevels(levels: Vector<Double>) {
        dataSource.saveBrightnessLevels(levels)
    }

    private fun defaultLevels() : Vector<Double> {
        return Vector<Double>().apply {
            add(50.0 / 100)
            add(1.0 / 100)
        }
    }

    companion object {
        const val MIN_BRIGHTNESS_LEVELS = 2
        const val MAX_BRIGHTNESS_LEVELS = 10
        const val BRIGHTNESS_LEVEL_DEFAULT = 0.5
        const val BRIGHTNESS_LEVEL_AUTO = -1.0
        private const val BRIGHTNESS_LEVELS_FILENAME = "brightnesslevels.dat"
    }
}