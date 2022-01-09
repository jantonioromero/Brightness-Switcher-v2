package com.arreis.brightnessswitcher.configuration

import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.arreis.brightnessswitcher.CWidgetProvider
import com.arreis.brightnessswitcher.CWidgetReceiver
import com.arreis.brightnessswitcher.MainViewModel
import com.arreis.brightnessswitcher.R
import com.arreis.brightnessswitcher.configuration.dialogs.ConfirmDeleteLevelDialog
import com.arreis.brightnessswitcher.configuration.dialogs.EditLevelDialog
import com.arreis.brightnessswitcher.configuration.dialogs.MessageDialog
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel.Auto
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel.FixedValue
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()

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

        mBrightnessLevels = viewModel.brightnessLevelRepository.getBrightnessLevels().toMutableList()
        mListView = findViewById<View>(R.id.config_list) as ListView
        mListView!!.adapter = LevelListAdapter(this, mBrightnessLevels)
        mListView!!.onItemClickListener =
            OnItemClickListener { arg0, arg1, position, arg3 ->
                mSelectedLevel = position
                EditLevelDialog.newInstance(mBrightnessLevels!![position].value).show(
                    supportFragmentManager, DIALOG_TAG_EDIT
                )
            }
        mListView!!.onItemLongClickListener =
            OnItemLongClickListener { arg0, arg1, position, arg3 ->
                if (mBrightnessLevels!!.size > BrightnessLevelRepository.MIN_BRIGHTNESS_LEVELS) {
                    mSelectedLevel = position
                    ConfirmDeleteLevelDialog().show(supportFragmentManager, DIALOG_TAG_CONFIRM_DELETE)
                } else {
                    MessageDialog.newInstance(getString(R.string.minimumLevelsMessage)).show(
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
            EditLevelDialog.newInstance(BrightnessLevelRepository.BRIGHTNESS_LEVEL_DEFAULT)
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

    fun doDeleteSelectedLevel() {
        mBrightnessLevels?.removeAt(mSelectedLevel)
        viewModel.brightnessLevelRepository.saveBrightnessLevels(mBrightnessLevels!!)
        updateUI()
    }

    fun doEditSelectedLevel(_newLevel: Double) {
        if (mSelectedLevel == NEW_LEVEL_INDEX) {
            mBrightnessLevels?.add(createBrightnessLevel(_newLevel))
        } else {
            mBrightnessLevels?.set(mSelectedLevel, createBrightnessLevel(_newLevel))
        }
        viewModel.brightnessLevelRepository.saveBrightnessLevels(mBrightnessLevels!!)
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
        (mListView!!.adapter as LevelListAdapter).notifyDataSetChanged()
    }

    private fun updateWidget() {
        val intent = Intent(this, CWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = intArrayOf(R.xml.app_widget_info)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }

}