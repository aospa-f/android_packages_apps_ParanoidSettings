/*
 * SPDX-FileCopyrightText: 2026 The AOSPA Project
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.display;

import android.content.Context;

import com.android.settings.core.BasePreferenceController;

public class StatusBarClockPreferenceController extends BasePreferenceController {

    public StatusBarClockPreferenceController(Context context, String preferenceKey) {
        super(context, preferenceKey);
    }

    @Override
    public int getAvailabilityStatus() {
        return AVAILABLE;
    }
}
