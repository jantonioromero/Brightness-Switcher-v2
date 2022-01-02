package com.arreis.brightnessswitcher.repository

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.arreis.brightnessswitcher.data.BrightnessLevelFileDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BrightnessLevelRepositoryTest {

    lateinit var appContext: Context

    lateinit var repository: BrightnessLevelRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val dataSource = BrightnessLevelFileDataSource(appContext)
        dataSource.resetBrightnessLevels()
        repository = BrightnessLevelRepository(dataSource)
    }

    @Test
    fun defaultBrightnessLevels() {
        val defaultLevels = defaultLevels()
        val levels = repository.getBrightnessLevels()
        assertEquals(defaultLevels, levels)
    }

    private fun defaultLevels(): List<BrightnessLevel> {
        return listOf(
            BrightnessLevel.FixedValue(0.5),
            BrightnessLevel.FixedValue(0.01))
    }

    @Test
    fun saveBrightnessLevels() {
        val expectedLevels = defaultLevels().toMutableList()
        expectedLevels.add(BrightnessLevel.FixedValue(0.95))
        repository.saveBrightnessLevels(expectedLevels)
        val levels = repository.getBrightnessLevels()
        assertEquals(expectedLevels, levels)
    }
}