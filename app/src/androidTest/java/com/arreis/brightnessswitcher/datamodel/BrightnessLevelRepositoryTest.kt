package com.arreis.brightnessswitcher.datamodel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Vector

@RunWith(AndroidJUnit4::class)
class BrightnessLevelRepositoryTest {

    lateinit var appContext: Context

    lateinit var repository: BrightnessLevelRepository

    @Before
    @Throws(Exception::class)
    fun setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        repository = BrightnessLevelRepository(appContext)
        repository.resetBrightnessLevels()
    }

    @Test
    fun defaultBrightnessLevels() {
        val defaultLevels = defaultLevels()
        val levels = repository.getBrightnessLevels()
        Assert.assertEquals(defaultLevels, levels)
    }

    private fun defaultLevels(): Vector<Double> {
        val defaultLevels = Vector<Double>()
        defaultLevels.add(0.5)
        defaultLevels.add(0.01)
        return defaultLevels
    }

    @Test
    fun saveBrightnessLevels() {
        val expectedLevels = defaultLevels()
        expectedLevels.add(0.95)
        repository.saveBrightnessLevels(expectedLevels)
        val levels = repository.getBrightnessLevels()
        Assert.assertEquals(expectedLevels, levels)
    }
}