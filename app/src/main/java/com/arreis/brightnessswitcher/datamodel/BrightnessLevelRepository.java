package com.arreis.brightnessswitcher.datamodel;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

public class BrightnessLevelRepository
{
	public static final int MIN_BRIGHTNESS_LEVELS = 2;
	public static final int MAX_BRIGHTNESS_LEVELS = 10;
	public static final double BRIGHTNESS_LEVEL_DEFAULT = 0.5;
	public static final double BRIGHTNESS_LEVEL_AUTO = -1.0;
	
	private static final String BRIGHTNESS_LEVELS_FILENAME = "brightnesslevels.dat";
	
	private static Vector<Double> brightnessLevels;

	public void resetBrightnessLevels(Context context) {
		brightnessLevels = null;
		context.deleteFile(BRIGHTNESS_LEVELS_FILENAME);
	}

	public Vector<Double> getBrightnessLevels(Context context)
	{
		if (brightnessLevels == null)
		{
			loadBrightnessLevels(context);
		}

		return brightnessLevels;
	}

	private void loadBrightnessLevels(Context context)
	{
		try
		{
			FileInputStream fis = context.openFileInput(BRIGHTNESS_LEVELS_FILENAME);
			StringBuilder buffer = new StringBuilder();
			String Read;
			BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
			if (fis != null)
			{
				while ((Read = reader.readLine()) != null)
				{
					buffer.append(Read).append("\n");
				}
			}
			if (fis != null) {
				fis.close();
			}

			JSONArray entries = new JSONArray(buffer.toString());

			brightnessLevels = new Vector<>();
			for (int i = 0; i < entries.length(); i++)
			{
				brightnessLevels.add(entries.getDouble(i));
			}
		} catch (FileNotFoundException e)
		{
			loadDefaultLevels();
		} catch (NumberFormatException | IOException | JSONException e)
		{
			e.printStackTrace();
		}
	}

	public void saveBrightnessLevels(Context context, Vector<Double> levels)
	{
		try
		{
			JSONArray values = new JSONArray();
			for (int i = 0; i < levels.size(); i++)
			{
				values.put(levels.get(i));
			}

			FileOutputStream fos = context.openFileOutput(BRIGHTNESS_LEVELS_FILENAME, Context.MODE_PRIVATE);
			fos.write(values.toString().getBytes());
			fos.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private void loadDefaultLevels()
	{
		brightnessLevels = new Vector<>();
		brightnessLevels.add(50.0 / 100);
		brightnessLevels.add(1.0 / 100);
	}
}
