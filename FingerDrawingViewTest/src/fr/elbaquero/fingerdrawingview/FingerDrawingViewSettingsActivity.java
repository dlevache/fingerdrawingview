/*
 * Copyright 2014 elbaquero
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package fr.elbaquero.fingerdrawingview;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * {@link FingerDrawingView} settings activity.
 */
public class FingerDrawingViewSettingsActivity extends Activity
{
    /** Drawing pen width edit text. */
    private EditText mDrawingPenWidthValue;

    /** Erasing pen width edit text. */
    private EditText mErasingPenWidthValue;

    /** Pencil color value. */
    private View mDrawingPenColorValue;

    /** Pencil color red value. */
    private SeekBar mDrawingPenColorRedValue;

    /** Pencil color green value. */
    private SeekBar mDrawingPenColorGreenValue;

    /** Pencil color blue value. */
    private SeekBar mDrawingPenColorBlueValue;

    /** Pencil color alpha value. */
    private SeekBar mDrawingPenColorAlphaValue;

    /** Background color value. */
    private View mBackgroundColorValue;

    /** Background color red value. */
    private SeekBar mBackgroundColorRedValue;

    /** Background color green value. */
    private SeekBar mBackgroundColorGreenValue;

    /** Background color blue value. */
    private SeekBar mBackgroundColorBlueValue;

    /** Background color alpha value. */
    private SeekBar mBackgroundColorAlphaValue;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        View settingsView = findViewById(R.id.fab_settings);
        settingsView.setVisibility(View.GONE);

        final View drawingPenColorSettingView = findViewById(R.id.settings_drawing_color);
        final View backgroundColorSettingView = findViewById(R.id.settings_background_color);
        final View drawingPenColorEditLayout = (LinearLayout) findViewById(R.id.settings_drawing_color_edit);
        final View backgroundColorEditLayout = (LinearLayout) findViewById(R.id.settings_background_color_edit);

        mDrawingPenWidthValue = (EditText) findViewById(R.id.settings_drawing_pen_width_value);
        mErasingPenWidthValue = (EditText) findViewById(R.id.settings_erasing_pen_width_value);
        mDrawingPenColorValue = findViewById(R.id.settings_drawing_color_value);
        mDrawingPenColorRedValue = (SeekBar) findViewById(R.id.settings_drawing_color_red);
        mDrawingPenColorGreenValue = (SeekBar) findViewById(R.id.settings_drawing_color_green);
        mDrawingPenColorBlueValue = (SeekBar) findViewById(R.id.settings_drawing_color_blue);
        mDrawingPenColorAlphaValue = (SeekBar) findViewById(R.id.settings_drawing_color_alpha);

        mBackgroundColorValue = findViewById(R.id.settings_background_color_value);
        mBackgroundColorRedValue = (SeekBar) findViewById(R.id.settings_background_color_red);
        mBackgroundColorGreenValue = (SeekBar) findViewById(R.id.settings_background_color_green);
        mBackgroundColorBlueValue = (SeekBar) findViewById(R.id.settings_background_color_blue);
        mBackgroundColorAlphaValue = (SeekBar) findViewById(R.id.settings_background_color_alpha);

        OnClickListener onClickListener = new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                switch (v.getId())
                {
                    case R.id.settings_drawing_color:
                        if (drawingPenColorEditLayout.getVisibility() == View.GONE)
                        {
                            drawingPenColorEditLayout.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            drawingPenColorEditLayout.setVisibility(View.GONE);
                        }
                        break;

                    case R.id.settings_background_color:
                        if (backgroundColorEditLayout.getVisibility() == View.GONE)
                        {
                            backgroundColorEditLayout.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            backgroundColorEditLayout.setVisibility(View.GONE);
                        }
                        break;

                    default:
                        break;
                }
            }
        };

        drawingPenColorSettingView.setOnClickListener(onClickListener);
        backgroundColorSettingView.setOnClickListener(onClickListener);

        OnSeekBarChangeListener onSeekBarChangeListener = new OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(final SeekBar seekBar)
            {
            }

            @Override
            public void onStartTrackingTouch(final SeekBar seekBar)
            {
            }

            @Override
            public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser)
            {
                switch (seekBar.getId())
                {
                    case R.id.settings_drawing_color_alpha:
                    case R.id.settings_drawing_color_red:
                    case R.id.settings_drawing_color_green:
                    case R.id.settings_drawing_color_blue:
                        int drawingPenColor = buildDrawingPenColor();
                        mDrawingPenColorValue.setBackgroundColor(drawingPenColor);
                        break;

                    case R.id.settings_background_color_alpha:
                    case R.id.settings_background_color_red:
                    case R.id.settings_background_color_green:
                    case R.id.settings_background_color_blue:
                        int backgroundColor = buildBackgroundColor();
                        mBackgroundColorValue.setBackgroundColor(backgroundColor);
                        break;

                    default:
                        break;
                }
            }
        };

        mDrawingPenColorRedValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mDrawingPenColorGreenValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mDrawingPenColorBlueValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mDrawingPenColorAlphaValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mBackgroundColorRedValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mBackgroundColorGreenValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mBackgroundColorBlueValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
        mBackgroundColorAlphaValue.setOnSeekBarChangeListener(onSeekBarChangeListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        PreferencesManager preferencesManager = PreferencesManager.getInstance(this);
        mDrawingPenWidthValue.setText(String.valueOf(preferencesManager.readInteger(
                Constants.PREFERENCES_KEY_DRAWING_PEN_WIDTH, Constants.PREFERENCES_DEFAULT_DRAWING_PEN_WIDTH)));
        mErasingPenWidthValue.setText(String.valueOf(preferencesManager.readInteger(
                Constants.PREFERENCES_KEY_ERASING_PEN_WIDTH, Constants.PREFERENCES_DEFAULT_ERASING_PEN_WIDTH)));

        int color = preferencesManager.readInteger(Constants.PREFERENCES_KEY_DRAWING_PEN_COLOR,
                Constants.PREFERENCES_DEFAULT_DRAWING_PEN_COLOR);
        mDrawingPenColorValue.setBackgroundColor(color);

        int red = (color >> 16) & 0xFF;
        int green = (color >> 8) & 0xFF;
        int blue = color & 0xFF;
        int alpha = (color >> 24) & 0xFF;
        mDrawingPenColorRedValue.setProgress(red);
        mDrawingPenColorGreenValue.setProgress(green);
        mDrawingPenColorBlueValue.setProgress(blue);
        mDrawingPenColorAlphaValue.setProgress(alpha);

        color = preferencesManager.readInteger(Constants.PREFERENCES_KEY_BACKGROUND_COLOR,
                Constants.PREFERENCES_DEFAULT_BACKGROUND_COLOR);
        mBackgroundColorValue.setBackgroundColor(color);

        red = (color >> 16) & 0xFF;
        green = (color >> 8) & 0xFF;
        blue = color & 0xFF;
        alpha = (color >> 24) & 0xFF;
        mBackgroundColorRedValue.setProgress(red);
        mBackgroundColorGreenValue.setProgress(green);
        mBackgroundColorBlueValue.setProgress(blue);
        mBackgroundColorAlphaValue.setProgress(alpha);
    }

    @Override
    public void onBackPressed()
    {
        PreferencesManager preferencesManager = PreferencesManager.getInstance(this);

        preferencesManager.saveInteger(Constants.PREFERENCES_KEY_DRAWING_PEN_WIDTH,
                Integer.parseInt(mDrawingPenWidthValue.getText().toString()));
        preferencesManager.saveInteger(Constants.PREFERENCES_KEY_ERASING_PEN_WIDTH,
                Integer.parseInt(mErasingPenWidthValue.getText().toString()));

        int color = buildDrawingPenColor();
        preferencesManager.saveInteger(Constants.PREFERENCES_KEY_DRAWING_PEN_COLOR, color);

        color = buildBackgroundColor();
        preferencesManager.saveInteger(Constants.PREFERENCES_KEY_BACKGROUND_COLOR, color);

        super.onBackPressed();
    }

    /**
     * Build the drawing pen color using the seek bars progress values.
     * 
     * @return the drawing pen color.
     */
    private int buildDrawingPenColor()
    {
        int red = mDrawingPenColorRedValue.getProgress();
        int green = mDrawingPenColorGreenValue.getProgress();
        int blue = mDrawingPenColorBlueValue.getProgress();
        int alpha = mDrawingPenColorAlphaValue.getProgress();

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    /**
     * Build the background color using the seek bars progress values.
     * 
     * @return the background color.
     */
    private int buildBackgroundColor()
    {
        int red = mBackgroundColorRedValue.getProgress();
        int green = mBackgroundColorGreenValue.getProgress();
        int blue = mBackgroundColorBlueValue.getProgress();
        int alpha = mBackgroundColorAlphaValue.getProgress();

        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
}
