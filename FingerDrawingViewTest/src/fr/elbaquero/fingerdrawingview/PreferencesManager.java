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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Preferences manager.
 */
public final class PreferencesManager
{
    /** PreferencesManager instance. */
    private static PreferencesManager sSingleInstance;

    /** Shared preferences. */
    private SharedPreferences mSharedPreferences;

    /**
     * Private constructor.
     * 
     * @param context
     *            the application context.
     */
    private PreferencesManager(final Context context)
    {
        String appPackage = context.getPackageName();
        mSharedPreferences = context.getSharedPreferences(appPackage, Context.MODE_PRIVATE);
    }

    /**
     * @param context
     *            the application context.
     * @return the unique {@link PreferencesManager} instance.
     */
    public static PreferencesManager getInstance(final Context context)
    {
        if (sSingleInstance == null)
        {
            sSingleInstance = new PreferencesManager(context);
        }

        return sSingleInstance;
    }

    /**
     * Read an integer in the {@link SharedPreferences}.
     * 
     * @param key
     *            the integer storage key.
     * @param defaultValue
     *            the integer default value.
     * @return the read value.
     */
    public int readInteger(final String key, final int defaultValue)
    {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Save an float into the {@link SharedPreferences}.
     * 
     * @param key
     *            the float storage key.
     * @param value
     *            the float value.
     */
    public void saveFloat(final String key, final float value)
    {
        Editor editor = mSharedPreferences.edit();
        editor.putFloat(key, value);
        editor.commit();
    }

    /**
     * Read an float in the {@link SharedPreferences}.
     * 
     * @param key
     *            the float storage key.
     * @param defaultValue
     *            the float default value.
     * @return the read value.
     */
    public float readFloat(final String key, final float defaultValue)
    {
        return mSharedPreferences.getFloat(key, defaultValue);
    }

    /**
     * Save an integer into the {@link SharedPreferences}.
     * 
     * @param key
     *            the integer storage key.
     * @param value
     *            the integer value.
     */
    public void saveInteger(final String key, final int value)
    {
        Editor editor = mSharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    /**
     * Read a boolean in the {@link SharedPreferences}.
     * 
     * @param key
     *            the boolean storage key.
     * @param defaultValue
     *            the boolean default value.
     * @return the read value.
     */
    public boolean readBoolean(final String key, final boolean defaultValue)
    {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Save an boolean into the {@link SharedPreferences}.
     * 
     * @param key
     *            the boolean storage key.
     * @param value
     *            the boolean value.
     */
    public void saveBoolean(final String key, final boolean value)
    {
        Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
}
