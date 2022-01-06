package com.arreis.brightnessswitcher.configuration

import android.app.AlertDialog
import android.app.Dialog
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.arreis.brightnessswitcher.CWidgetProvider
import com.arreis.brightnessswitcher.CWidgetReceiver
import com.arreis.brightnessswitcher.MainViewModel
import com.arreis.brightnessswitcher.R
import com.arreis.brightnessswitcher.data.BrightnessLevelFileDataSource
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel.Auto
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel.FixedValue
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private var mListView: ListView? = null
    private var mShowTitleCheck: CheckBox? = null
    private var mAddLevelButton: Button? = null
    private var mConfigFinishedButton: Button? = null

    private var mBrightnessLevels: MutableList<BrightnessLevel>? = null
    private var mSelectedLevel = 0

    private var mAppWidgetId = 0

    private val NEW_LEVEL_INDEX = -1

    private val DIALOG_TAG_CONFIRM_DELETE = "DIALOG_TAG_CONFIRM_DELETE"
    private val DIALOG_TAG_EDIT = "DIALOG_TAG_EDIT"
    private val DIALOG_TAG_MESSAGE = "DIALOG_TAG_MESSAGE"

    private var brightnessLevelRepository: BrightnessLevelRepository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val extras = intent.extras
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            if (mAppWidgetId != 0) {
                setResult(RESULT_CANCELED)
            }
        }
        if (resources.getBoolean(R.bool.allowLandscape) == false) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setContentView(R.layout.activity_main)
        if (brightnessLevelRepository == null) {
            brightnessLevelRepository = BrightnessLevelRepository(
                BrightnessLevelFileDataSource(
                    applicationContext
                )
            )
        }
        mBrightnessLevels = brightnessLevelRepository!!.getBrightnessLevels().toMutableList()
        mListView = findViewById<View>(R.id.config_list) as ListView
        mListView!!.adapter = CConfigurationListAdapter(this, mBrightnessLevels)
        mListView!!.onItemClickListener =
            OnItemClickListener { arg0, arg1, position, arg3 ->
                mSelectedLevel = position
                CEditLevelDialog.newInstance(mBrightnessLevels!![position].value).show(
                    supportFragmentManager, DIALOG_TAG_EDIT
                )
            }
        mListView!!.onItemLongClickListener =
            OnItemLongClickListener { arg0, arg1, position, arg3 ->
                if (mBrightnessLevels!!.size > BrightnessLevelRepository.MIN_BRIGHTNESS_LEVELS) {
                    mSelectedLevel = position
                    CConfirmDeleteDialog().show(supportFragmentManager, DIALOG_TAG_CONFIRM_DELETE)
                } else {
                    CMessageDialog.newInstance(getString(R.string.minimumLevelsMessage)).show(
                        supportFragmentManager, DIALOG_TAG_MESSAGE
                    )
                }
                true
            }
        val prefs = PreferenceManager.getDefaultSharedPreferences(
            applicationContext
        )
        mShowTitleCheck = findViewById<View>(R.id.config_check_showWidgetTitle) as CheckBox
        mShowTitleCheck!!.setOnCheckedChangeListener { buttonView, isChecked ->
            val edit = prefs.edit()
            edit.putBoolean(CWidgetReceiver.PREFERENCES_SHOW_WIDGET_TITLE, isChecked)
            edit.commit()
            updateWidget()
        }
        mShowTitleCheck!!.isChecked =
            prefs.getBoolean(CWidgetReceiver.PREFERENCES_SHOW_WIDGET_TITLE, true)
        mAddLevelButton = findViewById<View>(R.id.config_button_addLevel) as Button
        mAddLevelButton!!.setOnClickListener {
            mSelectedLevel = NEW_LEVEL_INDEX
            CEditLevelDialog.newInstance(BrightnessLevelRepository.BRIGHTNESS_LEVEL_DEFAULT)
                .show(
                    supportFragmentManager, DIALOG_TAG_EDIT
                )
        }
//        mConfigFinishedButton = findViewById<View>(R.id.configFinished_button) as Button
//        mConfigFinishedButton!!.visibility = if (mAppWidgetId == 0) View.GONE else View.VISIBLE
//        mConfigFinishedButton!!.setOnClickListener {
//            val context = applicationContext
//            val appWidgetManager = AppWidgetManager.getInstance(context)
//            val views = RemoteViews(context.packageName, R.layout.widget_layout)
//            appWidgetManager.updateAppWidget(mAppWidgetId, views)
//            val resultValue = Intent()
//            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId)
//            setResult(RESULT_OK, resultValue)
//            finish()
//        }
    }

    private fun doDeleteSelectedLevel() {
        mBrightnessLevels?.removeAt(mSelectedLevel)
        brightnessLevelRepository!!.saveBrightnessLevels(mBrightnessLevels!!)
        updateUI()
    }

    private fun doEditSelectedLevel(_newLevel: Double) {
        if (mSelectedLevel == NEW_LEVEL_INDEX) {
            mBrightnessLevels?.add(createBrightnessLevel(_newLevel))
        } else {
            mBrightnessLevels?.set(mSelectedLevel, createBrightnessLevel(_newLevel))
        }
        brightnessLevelRepository!!.saveBrightnessLevels(mBrightnessLevels!!)
        updateUI()
    }

    private fun createBrightnessLevel(value: Double): BrightnessLevel {
        return if (value == -1.0) {
            Auto()
        } else {
            FixedValue(value)
        }
    }

    private fun updateUI() {
        mAddLevelButton!!.isEnabled =
            mBrightnessLevels!!.size < BrightnessLevelRepository.MAX_BRIGHTNESS_LEVELS
        (mListView!!.adapter as CConfigurationListAdapter).notifyDataSetChanged()
    }

    private fun updateWidget() {
        val intent = Intent(this, CWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(R.xml.app_widget_info)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

    private class CConfigurationListAdapter(
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
            var res: CConfigurationCell? = null
            res =
                if (convertView != null && convertView.javaClass == CConfigurationCell::class.java) {
                    convertView as CConfigurationCell
                } else {
                    LayoutInflater.from(context)
                        .inflate(R.layout.cell_configuration, null) as CConfigurationCell
                }
            val level = getItem(position)
            val levelString =
                when (level) {
                    null -> ""
                    is Auto -> context.getString(R.string.auto)
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

    class CMessageDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(requireArguments().getString("message"))
                .setPositiveButton(R.string.ok, null)
            return builder.create()
        }

        companion object {
            fun newInstance(_message: String?): CMessageDialog {
                val f = CMessageDialog()
                val args = Bundle()
                args.putString("message", _message)
                f.arguments = args
                return f
            }
        }
    }

    class CConfirmDeleteDialog : DialogFragment() {
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            builder.setMessage(R.string.deleteLevelConfirmationMessage)
                .setNegativeButton(R.string.no, null).setPositiveButton(
                    R.string.yes
                ) { dialog, which -> (activity as MainActivity?)!!.doDeleteSelectedLevel() }
            return builder.create()
        }
    }

    class CEditLevelDialog : DialogFragment() {
        private var mLevelText: TextView? = null
        private var mLevelSeekBar: SeekBar? = null
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val initialLevel = requireArguments().getDouble("level")
            val view = LayoutInflater.from(activity).inflate(R.layout.dialog_editlevel, null)
            mLevelSeekBar = view.findViewById<View>(R.id.level_seekbar) as SeekBar
            mLevelSeekBar!!.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
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
            val builder = AlertDialog.Builder(activity)
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
            fun newInstance(_level: Double): CEditLevelDialog {
                val f = CEditLevelDialog()
                val args = Bundle()
                args.putDouble("level", _level)
                f.arguments = args
                return f
            }
        }
    }

}