package com.arreis.brightnessswitcher.datamodel

import android.content.Context
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.lang.NumberFormatException
import java.lang.StringBuilder
import java.util.Vector

class BrightnessLevelRepository(private val context: Context) {

    fun resetBrightnessLevels() {
        brightnessLevels = null
        context.deleteFile(BRIGHTNESS_LEVELS_FILENAME)
    }

    fun getBrightnessLevels(): Vector<Double>? {
        if (brightnessLevels == null) {
            loadBrightnessLevels()
        }
        return brightnessLevels
    }

    private fun loadBrightnessLevels() {
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
            brightnessLevels = Vector()
            for (i in 0 until entries.length()) {
                brightnessLevels!!.add(entries.getDouble(i))
            }
        } catch (e: FileNotFoundException) {
            loadDefaultLevels()
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun saveBrightnessLevels(levels: Vector<Double>) {
        try {
            val values = JSONArray()
            for (i in levels.indices) {
                values.put(levels[i])
            }
            val fos = context.openFileOutput(BRIGHTNESS_LEVELS_FILENAME, Context.MODE_PRIVATE)
            fos.write(values.toString().toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun loadDefaultLevels() {
        brightnessLevels = Vector()
        brightnessLevels!!.add(50.0 / 100)
        brightnessLevels!!.add(1.0 / 100)
    }

    companion object {
        const val MIN_BRIGHTNESS_LEVELS = 2
        const val MAX_BRIGHTNESS_LEVELS = 10
        const val BRIGHTNESS_LEVEL_DEFAULT = 0.5
        const val BRIGHTNESS_LEVEL_AUTO = -1.0
        private const val BRIGHTNESS_LEVELS_FILENAME = "brightnesslevels.dat"
        private var brightnessLevels: Vector<Double>? = null
    }
}