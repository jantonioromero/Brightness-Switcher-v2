package com.arreis.brightnessswitcher;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;

import com.arreis.brightnessswitcher.data.BrightnessLevelFileDataSource;
import com.arreis.brightnessswitcher.repository.BrightnessLevelRepository;
import com.arreis.brightnessswitcher.domain.entity.BrightnessLevel;

import java.util.List;

public class CWidgetReceiver extends BroadcastReceiver
{
	private static List<BrightnessLevel> mBrightnessLevels;
	private static int mCurrentLevelIndex;
	
	private static final String PREFERENCES_BRIGHTNESS_LEVEL_CURRENT = "PREFERENCES_BRIGHTNESS_LEVEL_CURRENT";
	public static final String PREFERENCES_SHOW_WIDGET_TITLE = "PREFERENCES_SHOW_WIDGET_TITLE";

	private BrightnessLevelRepository brightnessLevelRepository = null;
	
	@Override
	public void onReceive(Context context, Intent intent)
	{
		if (brightnessLevelRepository == null) {
			brightnessLevelRepository = new BrightnessLevelRepository(new BrightnessLevelFileDataSource(context));
		}

		if (mBrightnessLevels == null)
		{
			mBrightnessLevels = brightnessLevelRepository.getBrightnessLevels();
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mCurrentLevelIndex = prefs.getInt(PREFERENCES_BRIGHTNESS_LEVEL_CURRENT, -1);
		if (mCurrentLevelIndex < 0)
		{
			mCurrentLevelIndex = 0;
		}
		else
		{
			mCurrentLevelIndex = (mCurrentLevelIndex + 1) % mBrightnessLevels.size();
		}
		
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(PREFERENCES_BRIGHTNESS_LEVEL_CURRENT, mCurrentLevelIndex);
		editor.apply();
		
		double level = mBrightnessLevels.get(mCurrentLevelIndex).getValue();
		setBrightnessLevel(context, level);
		
		boolean showTitle = prefs.getBoolean(PREFERENCES_SHOW_WIDGET_TITLE, true);
		
		RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
		remoteViews.setTextViewText(R.id.widget_text, level == BrightnessLevelRepository.BRIGHTNESS_LEVEL_AUTO ? context.getString(R.string.auto) : String.format(context.getString(R.string.percentLevelFormat), Math.round(100 * level)));
		remoteViews.setViewVisibility(R.id.widget_title, showTitle ? View.VISIBLE : View.GONE);
		updateWidgetTextSize(context, remoteViews, showTitle);
		
		remoteViews.setOnClickPendingIntent(R.id.widget_button, CWidgetProvider.buildButtonPendingIntent(context));
		CWidgetProvider.pushWidgetUpdate(context.getApplicationContext(), remoteViews);
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void updateWidgetTextSize(Context context, RemoteViews remoteViews, boolean showTitle)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			float textSize = context.getResources().getDimension(showTitle ? R.dimen.widget_textSize_withTitle : R.dimen.widget_textSize);
			remoteViews.setTextViewTextSize(R.id.widget_text, TypedValue.COMPLEX_UNIT_PX, textSize);
		}
	}
	
	private void setBrightnessLevel(Context _context, double _level)
	{
		if (_level == BrightnessLevelRepository.BRIGHTNESS_LEVEL_AUTO)
		{
			Settings.System.putInt(_context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
		}
		else
		{
			Settings.System.putInt(_context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
			Settings.System.putInt(_context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, (int) (_level * 255));
		}
		
		// Force refresh
		Intent intent = new Intent(_context, CRefreshBrightnessActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("level", _level);
		_context.startActivity(intent);
	}
}
