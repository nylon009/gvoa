package com.gapp.gvoa.ui;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gapp.gvoa.datatype.RssFeed;
import com.gapp.gvoa.db.DbRssFeed;

public class RssFeedListActivity extends ListActivity{

	public static final String tag = "RssListActivity";
    private List<RssFeed> rssFeedList = null;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        rssFeedList = DbRssFeed.getAllFeeds();
        setListAdapter(new ArrayAdapter<RssFeed>(this, 
        		                                android.R.layout.simple_list_item_1,
        		                                rssFeedList));
        getListView().setTextFilterEnabled(true);
    }

	@Override
	public void onListItemClick(ListView l, View v, int position, long id)	{
		Log.i(tag, "item clicked! [" + rssFeedList.get(position).getTitle()
				+ "]");

		RssFeed feed = rssFeedList.get(position);
		
		Intent itemintent = new Intent(this, RssItemListActivity.class);

		Bundle bundle = new Bundle();
		bundle.putParcelable(RssFeed.class.getName(), feed);  
		itemintent.putExtras(bundle);
        startActivity(itemintent);		
	}
	


}
