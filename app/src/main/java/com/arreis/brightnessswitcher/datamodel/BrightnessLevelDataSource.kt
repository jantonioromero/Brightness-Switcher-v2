package com.arreis.brightnessswitcher.datamodel

import java.util.Vector

interface BrightnessLevelDataSource {
    fun resetBrightnessLevels()
    fun brightnessLevels(): Vector<Double>?
    fun saveBrightnessLevels(levels: Vector<Double>)
}