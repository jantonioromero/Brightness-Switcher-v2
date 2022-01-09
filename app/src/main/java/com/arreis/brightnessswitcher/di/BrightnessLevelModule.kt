package com.arreis.brightnessswitcher.di

import com.arreis.brightnessswitcher.MainViewModel
import com.arreis.brightnessswitcher.data.BrightnessLevelFileDataSource
import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository
import org.koin.dsl.module

val brightnessLevelModule = module {

    single<MainViewModel> { MainViewModel(get()) }

    single<BrightnessLevelRepository> { BrightnessLevelRepository(get()) }

    single<BrightnessLevelDataSource> { BrightnessLevelFileDataSource(get()) }
}
