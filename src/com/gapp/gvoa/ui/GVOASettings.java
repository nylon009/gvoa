package com.gapp.gvoa.ui;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.gapp.gvoa.R;

public class GVOASettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
