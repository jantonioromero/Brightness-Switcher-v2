package com.arreis.brightnessswitcher.data

import android.content.Context
import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import org.json.JSONException
import java.io.FileNotFoundException
import java.io.IOException

class BrightnessLevelFileDataSource(
    private val context: Context
    ) : BrightnessLevelDataSource {

    override fun resetBrightnessLevels() {
        context.deleteFile(BRIGHTNESS_LEVELS_FILENAME)
    }

    override fun brightnessLevels(): List<BrightnessLevel>? {
        try { context.openFileInput(BRIGHTNESS_LEVELS_FILENAME).bufferedReader().use {
                return it.readLines().map { it.toDouble() }.map {
                    when (it) {
                        -1.0 -> BrightnessLevel.Auto()
                        else -> BrightnessLevel.FixedValue(it)
                    }
                }.toList()
            }
        } catch (e: FileNotFoundException) {
        } catch (e: NumberFormatException) {
        } catch (e: IOException) {
        } catch (e: JSONException) {
        }
        return null
    }

    override fun saveBrightnessLevels(levels: List<BrightnessLevel>) {
        try {
            context.openFileOutput(BRIGHTNESS_LEVELS_FILENAME, Context.MODE_PRIVATE)
                .bufferedWriter().use {
                    levels.map { "${it.value}\n" }.forEach { level ->
                        it.write(level)
                    }
                }
        } catch (e: IOException) {
        }
    }

    companion object {
        private const val BRIGHTNESS_LEVELS_FILENAME = "brightnesslevels.dat"
    }
}