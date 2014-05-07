package com.gapp.gvoa;


import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.db.GRSSDbHandler;
import com.gapp.gvoa.ui.GVOASettings;
import com.gapp.gvoa.ui.RssFeedListActivity;
import com.gapp.gvoa.util.GPreference;
import com.gapp.gvoa.util.GvoaUtil;

public class MainActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);       
        
        this.globalInit();
        
        setTitle(R.string.app_name);
                
        
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
    
    public void globalInit()
    {
    	//init database
        GRSSDbHandler.initInstance(this.getBaseContext());        
        GPreference.init(this);
        
        this.clearOldCache();   
        
    }
    
    private void clearOldCache()
    {
    	int days = GPreference.getPreferredExpireDays();
    	
    	List<RssItem>  rssItemList = DbRssItem.getAllItems(-1);     	
    	
    	for (RssItem item: rssItemList){
    		SimpleDateFormat fmt =new SimpleDateFormat("yyyy-MM-dd");
        	try {
				Date date1 = fmt.parse(item.getPubDate());
				Date dateNow = new Date();				
				long daysBetween = TimeUnit.MILLISECONDS.toDays(dateNow.getTime() - date1.getTime());
				
				if(daysBetween >= days)
				{
					if(item.getLocalmp3()!=null && GvoaUtil.isFileExists(item.getLocalmp3()))
					{
		    		    File file = new File(item.getLocalmp3());
	    			    file.delete();
					}				
					DbRssItem.deleteItem(item);
				}				
				
			} catch (ParseException e) {
				e.printStackTrace();
			}            	
    		
    	}
    		
    	
    }
    
    


}
