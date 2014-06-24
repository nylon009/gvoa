package com.gapp.gvoa.ui;


import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.gapp.gvoa.R;

public class GVOASettings extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
        
        Preference preference = findPreference("pref_about");
        try {
			String versionName = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			preference.setSummary("GVOA Version: "+versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
         
        
    }
    
    
    
    
}
