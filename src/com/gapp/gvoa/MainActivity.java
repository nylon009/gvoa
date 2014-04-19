package com.gapp.gvoa;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.gapp.gvoa.db.GRSSDbHandler;
import com.gapp.gvoa.ui.GVOASettings;
import com.gapp.gvoa.ui.RssFeedListActivity;

public class MainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
        setTitle(R.string.app_name);
        
        //init database
        GRSSDbHandler.initInstance(this.getBaseContext());
        
        setContentView(R.layout.main_tab);
        
        /* TabHost will have Tabs */
        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
               
        /* tid1 is firstTabSpec Id. Its used to access outside. */
        TabSpec firstTabSpec = tabHost.newTabSpec("RSS");
        TabSpec secondTabSpec = tabHost.newTabSpec("Setting");
        
        firstTabSpec.setIndicator("RSS").setContent(new Intent(this,RssFeedListActivity.class));
        secondTabSpec.setIndicator("Setting").setContent(new Intent(this,GVOASettings.class));
        
        
        /* Add tabSpec to the TabHost to display. */
        tabHost.addTab(firstTabSpec);
        tabHost.addTab(secondTabSpec);
        
        tabHost.setCurrentTab(0);
    }


}
