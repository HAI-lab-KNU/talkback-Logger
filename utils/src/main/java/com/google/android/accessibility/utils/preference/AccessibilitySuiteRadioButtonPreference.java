/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.accessibility.utils.preference;

import android.content.Context;
import android.util.AttributeSet;
import androidx.preference.CheckBoxPreference;
import org.checkerframework.checker.nullness.qual.Nullable;

/** The class is used to inherit the {@link CheckBoxPreference} for the outsourcing project. */
public class AccessibilitySuiteRadioButtonPreference extends CheckBoxPreference {

  public AccessibilitySuiteRadioButtonPreference(Context context) {
    super(context);
  }

  public AccessibilitySuiteRadioButtonPreference(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
  }
}
