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

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * {@link FingerDrawingView} test activity.
 */
public class FingerDrawingViewTestActivity extends Activity
{
    /** Root view. */
    private View mRootView;

    /** Magnifying view. */
    private FingerDrawingView mFingerDrawingView;

    @Override
    public void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mRootView = findViewById(R.id.test_root);
        mFingerDrawingView = (FingerDrawingView) findViewById(R.id.test_drawing_view);
        mFingerDrawingView.startDrawingMode();

        View settingsView = findViewById(R.id.fab_settings);
        View drawingModeButton = findViewById(R.id.fabb_drawing_mode);
        View erasingModeButton = findViewById(R.id.fabb_erasing_mode);
        View eraseAllButton = findViewById(R.id.fabb_erase_all);
        View saveFileButton = findViewById(R.id.fabb_save_file);

        OnClickListener onClickListener = new OnClickListener()
        {
            @Override
            public void onClick(final View v)
            {
                switch (v.getId())
                {
                    case R.id.fab_settings:
                        Intent settingsIntent = new Intent(FingerDrawingViewTestActivity.this,
                                FingerDrawingViewSettingsActivity.class);
                        startActivity(settingsIntent);
                        break;

                    case R.id.fabb_drawing_mode:
                        mFingerDrawingView.startDrawingMode();
                        break;

                    case R.id.fabb_erasing_mode:
                        mFingerDrawingView.startErasingMode();
                        break;

                    case R.id.fabb_erase_all:
                        mFingerDrawingView.eraseAll();
                        break;

                    case R.id.fabb_save_file:
                        saveViewAsFile();
                        break;

                    default:
                        break;
                }

            }
        };

        settingsView.setOnClickListener(onClickListener);
        drawingModeButton.setOnClickListener(onClickListener);
        erasingModeButton.setOnClickListener(onClickListener);
        eraseAllButton.setOnClickListener(onClickListener);
        saveFileButton.setOnClickListener(onClickListener);

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        PreferencesManager preferencesManager = PreferencesManager.getInstance(this);
        mFingerDrawingView.setDrawingPenWidth(preferencesManager.readInteger(
                Constants.PREFERENCES_KEY_DRAWING_PEN_WIDTH, Constants.PREFERENCES_DEFAULT_DRAWING_PEN_WIDTH));
        mFingerDrawingView.setErasingPenWidth(preferencesManager.readInteger(
                Constants.PREFERENCES_KEY_ERASING_PEN_WIDTH, Constants.PREFERENCES_DEFAULT_ERASING_PEN_WIDTH));
        mFingerDrawingView.setDrawingPenColor(preferencesManager.readInteger(
                Constants.PREFERENCES_KEY_DRAWING_PEN_COLOR, Constants.PREFERENCES_DEFAULT_DRAWING_PEN_COLOR));
        mRootView.setBackgroundColor(preferencesManager.readInteger(Constants.PREFERENCES_KEY_BACKGROUND_COLOR,
                Constants.PREFERENCES_DEFAULT_BACKGROUND_COLOR));
    }

    /**
     * Save the view as a file.
     */
    private void saveViewAsFile()
    {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath()
                + File.separator + getPackageName() + File.separator;
        File destFolder = new File(path);
        destFolder.mkdirs();

        path = path + System.currentTimeMillis() + ".png";
        mFingerDrawingView.saveAsFile(path);

    }
}
