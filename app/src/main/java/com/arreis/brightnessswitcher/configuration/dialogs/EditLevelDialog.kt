package com.arreis.brightnessswitcher.configuration.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.arreis.brightnessswitcher.R
import com.arreis.brightnessswitcher.configuration.MainActivity
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository

class EditLevelDialog : DialogFragment() {
        private var mLevelText: TextView? = null
        private var mLevelSeekBar: SeekBar? = null
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val initialLevel = requireArguments().getDouble("level")
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_editlevel, null)
            mLevelSeekBar = view.findViewById<View>(R.id.level_seekbar) as SeekBar
            mLevelSeekBar!!.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(seekBar: SeekBar) {}
                override fun onStartTrackingTouch(seekBar: SeekBar) {}
                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                    mLevelText!!.text = String.format(
                        getString(R.string.textLevelFormat),
                        String.format(getString(R.string.percentLevelFormat), progress + 1)
                    )
                }
            })
            mLevelText = view.findViewById<View>(R.id.level_text) as TextView
            if (initialLevel == BrightnessLevelRepository.BRIGHTNESS_LEVEL_AUTO) {
                mLevelSeekBar!!.progress = 100 / 2
                mLevelText!!.text =
                    String.format(getString(R.string.textLevelFormat), getString(R.string.auto))
            } else {
                val levelPercent = (100 * initialLevel).toInt()
                mLevelSeekBar!!.progress = levelPercent - 1
                mLevelText!!.text = String.format(
                    getString(R.string.textLevelFormat),
                    String.format(getString(R.string.percentLevelFormat), levelPercent)
                )
            }
            val builder = AlertDialog.Builder(requireContext())
            builder.setView(view).setNegativeButton(R.string.cancel, null).setNeutralButton(
                R.string.auto
            ) { dialog, which ->
                (activity as MainActivity?)!!.doEditSelectedLevel(
                    BrightnessLevelRepository.BRIGHTNESS_LEVEL_AUTO
                )
            }.setPositiveButton(
                R.string.ok
            ) { dialog, which -> (activity as MainActivity?)!!.doEditSelectedLevel((mLevelSeekBar!!.progress + 1).toDouble() / 100) }
            val res = builder.create()
            res.setCanceledOnTouchOutside(false)
            return res
        }

        companion object {
            fun newInstance(_level: Double): EditLevelDialog {
                val f = EditLevelDialog()
                val args = Bundle()
                args.putDouble("level", _level)
                f.arguments = args
                return f
            }
        }
    }
