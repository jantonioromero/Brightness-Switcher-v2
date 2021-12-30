package com.arreis.brightnessswitcher.domain.entity

sealed class BrightnessLevel(open val value: Double) {

    data class Auto(override val value: Double = -1.0): BrightnessLevel(value)

    data class FixedValue(override val value: Double): BrightnessLevel(value)
}
