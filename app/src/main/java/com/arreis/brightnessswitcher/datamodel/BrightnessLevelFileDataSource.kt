package com.arreis.brightnessswitcher.datamodel

import android.content.Context
import com.arreis.brightnessswitcher.domain.datasource.BrightnessLevelDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader

class BrightnessLevelFileDataSource(private val context: Context) : BrightnessLevelDataSource {

    override fun resetBrightnessLevels() {
        context.deleteFile(BRIGHTNESS_LEVELS_FILENAME)
    }

    override fun brightnessLevels(): List<BrightnessLevel>? {
        try {
            val fis = context.openFileInput(BRIGHTNESS_LEVELS_FILENAME)
            val buffer = StringBuilder()
            var Read: String?
            val reader = BufferedReader(InputStreamReader(fis))
            if (fis != null) {
                while (reader.readLine().also { Read = it } != null) {
                    buffer.append(Read).append("\n")
                }
            }
            fis?.close()
            val entries = JSONArray(buffer.toString())
            val brightnessLevels = mutableListOf<BrightnessLevel>()
            for (i in 0 until entries.length()) {
                brightnessLevels.add(if (entries.getDouble(i) == -1.0) BrightnessLevel.Auto() else BrightnessLevel.FixedValue(entries.getDouble(i)))
            }
            return brightnessLevels
        } catch (e: FileNotFoundException) {
        } catch (e: NumberFormatException) {
        } catch (e: IOException) {
        } catch (e: JSONException) {
        }

        return null
    }

    override fun saveBrightnessLevels(levels: List<BrightnessLevel>) {
        try {
            val values = JSONArray()
            for (level in levels) {
                values.put(level.value)
            }
            val fos = context.openFileOutput(BRIGHTNESS_LEVELS_FILENAME, Context.MODE_PRIVATE)
            fos.write(values.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
        }
    }

    companion object {
        private const val BRIGHTNESS_LEVELS_FILENAME = "brightnesslevels.dat"
    }
}