/*
 * Copyright (C) 2016 The Android Open Source Project
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

package com.android.setupwizard;

import static com.android.setupwizard.SetupWizardApp.DISABLE_NAV_KEYS;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.IWindowManager;
import android.view.View;
import android.view.WindowManagerGlobal;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.util.td.TDActionUtils;
import com.android.setupwizardlib.util.WizardManagerHelper;

import com.android.setupwizard.R;
import com.android.setupwizard.util.SetupWizardUtils;

public class TripNDroidSettingsActivity extends BaseSetupWizardActivity {

    public static final String TAG = TripNDroidSettingsActivity.class.getSimpleName();

    private SetupWizardApp mSetupWizardApp;

    private View mNavKeysRow;
    private CheckBox mNavKeys;

    private View.OnClickListener mNavKeysClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean checked = !mNavKeys.isChecked();
            mNavKeys.setChecked(checked);
            mSetupWizardApp.getSettingsBundle().putBoolean(DISABLE_NAV_KEYS, checked);
            Settings.System.putInt(getContentResolver(), Settings.System.NAVIGATION_BAR_VISIBLE, checked ? 1 : 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSetupWizardApp = (SetupWizardApp) getApplication();
        setNextText(R.string.next);

        mNavKeysRow = findViewById(R.id.nav_keys);
        mNavKeysRow.setOnClickListener(mNavKeysClickListener);
        mNavKeys = (CheckBox) findViewById(R.id.nav_keys_checkbox);
        boolean needsNavBar = true;
        try {
            IWindowManager windowManager = WindowManagerGlobal.getWindowManagerService();
            needsNavBar = windowManager.needsNavigationBar();
        } catch (RemoteException e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDisableNavkeysOption();
    }

    @Override
    public void onNavigateBack() {
        onBackPressed();
    }

    @Override
    public void onNavigateNext() {
        Intent intent = WizardManagerHelper.getNextIntent(getIntent(), Activity.RESULT_OK);
        startActivityForResult(intent, 1);
    }

    @Override
    protected int getTransition() {
        return TRANSITION_ID_SLIDE;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.setup_android_settings;
    }

    @Override
    protected int getTitleResId() {
        return R.string.setup_services;
    }

    @Override
    protected int getIconResId() {
        return R.drawable.ic_features;
    }

    private void updateDisableNavkeysOption() {
            final Bundle myPageBundle = mSetupWizardApp.getSettingsBundle();
            boolean enabled = Settings.System.getInt(getContentResolver(),
                    Settings.System.NAVIGATION_BAR_VISIBLE,
                    TDActionUtils.hasNavbarByDefault(this) ? 1 : 0) != 0;
            boolean checked = myPageBundle.containsKey(DISABLE_NAV_KEYS) ?
                    myPageBundle.getBoolean(DISABLE_NAV_KEYS) :
                    enabled;
            mNavKeys.setChecked(checked);
            myPageBundle.putBoolean(DISABLE_NAV_KEYS, checked);
    }
}
