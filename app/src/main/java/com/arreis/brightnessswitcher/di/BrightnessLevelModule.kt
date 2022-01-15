package com.arreis.brightnessswitcher.di

import com.arreis.brightnessswitcher.MainViewModel
import com.arreis.brightnessswitcher.data.BrightnessLevelFileDataSource
import com.arreis.brightnessswitcher.data.BrightnessLevelInMemoryDataSource
import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val brightnessLevelModule = module {

    fun defaultBrightnessLevels() = listOf(
        BrightnessLevel.FixedValue(50.0 / 100),
        BrightnessLevel.FixedValue(1.0 / 100)
    )

    factory<MainViewModel> { MainViewModel(get()) }

    factory<BrightnessLevelRepository> { BrightnessLevelRepository(get()) }

//    factory<BrightnessLevelDataSource> { BrightnessLevelFileDataSource(androidContext()) }
    factory<BrightnessLevelDataSource> { BrightnessLevelInMemoryDataSource(defaultBrightnessLevels()) }
}
