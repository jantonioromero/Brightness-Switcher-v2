package com.arreis.brightnessswitcher.domain.entity

sealed class BrightnessLevel {
    object Auto: BrightnessLevel()
    class FixedValue(private val value: Int): BrightnessLevel()
}
