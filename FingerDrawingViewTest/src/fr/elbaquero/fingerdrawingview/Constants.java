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

import android.graphics.Color;

/**
 * Constants.
 */
public class Constants
{
    /** Preferences key: drawing pen width. */
    public static final String PREFERENCES_KEY_DRAWING_PEN_WIDTH = "PREFERENCES_KEY_DRAWING_PEN_WIDTH";

    /** Preferences key: erasing pen width. */
    public static final String PREFERENCES_KEY_ERASING_PEN_WIDTH = "PREFERENCES_KEY_ERASING_PEN_WIDTH";

    /** Preferences key: drawing pen color. */
    public static final String PREFERENCES_KEY_DRAWING_PEN_COLOR = "PREFERENCES_KEY_DRAWING_PEN_COLOR";

    /** Preferences key: border width. */
    public static final String PREFERENCES_KEY_BACKGROUND_COLOR = "PREFERENCES_KEY_BACKGROUND_COLOR";

    /** Preferences default value: drawing pen width. */
    public static final int PREFERENCES_DEFAULT_DRAWING_PEN_WIDTH = 2;

    /** Preferences default value: erasing pen width. */
    public static final int PREFERENCES_DEFAULT_ERASING_PEN_WIDTH = 4;

    /** Preferences default value: drawing pen color. */
    public static final int PREFERENCES_DEFAULT_DRAWING_PEN_COLOR = Color.WHITE;

    /** Preferences default value: background color. */
    public static final int PREFERENCES_DEFAULT_BACKGROUND_COLOR = Color.BLACK;

    /**
     * Empty, private constructor.
     */
    private Constants()
    {
    }
}
