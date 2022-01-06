package com.arreis.brightnessswitcher.configuration

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.arreis.brightnessswitcher.R
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel

class LevelListAdapter(
    private val context: Context,
    private val mBrightnessLevels: List<BrightnessLevel>?
        ) : BaseAdapter() {

        override fun getCount(): Int {
            return mBrightnessLevels?.size ?: 0
        }

        override fun getItem(position: Int): BrightnessLevel? {
            return mBrightnessLevels?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var res: LevelView? = null
            res =
                if (convertView != null && convertView.javaClass == LevelView::class.java) {
                    convertView as LevelView
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.cell_configuration, null) as LevelView
                }
            val level = getItem(position)
            val levelString =
                when (level) {
                    null -> ""
                    is BrightnessLevel.Auto -> context.getString(R.string.auto)
                    else -> String.format(
                        context.getString(R.string.percentLevelFormat),
                        (level.value * 100).toInt()
                    )
                }
            res.setText(
                String.format(context.getString(R.string.cellIndexFormat), position + 1),
                levelString
            )
            return res!!
        }
    }
