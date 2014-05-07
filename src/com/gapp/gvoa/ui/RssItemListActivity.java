package com.gapp.gvoa.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssFeed;
import com.gapp.gvoa.datatype.RssItem;
import com.gapp.gvoa.db.DbRssItem;
import com.gapp.gvoa.parser.FeedParser;

public class RssItemListActivity extends  Activity implements OnItemClickListener {
    private static final int MSG_SUCCESS = 0;
    private static final int MSG_FAILURE = 1;
    
	public final String tag = "RssItemListActivity";
    private List<RssItem> rssItemList = null;
    
    private RssFeed  rssFeed = null;    

    private  ListView itemListView= null; 
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);            
        
        setContentView(R.layout.rss_item_list);
     
        rssFeed = getIntent().getParcelableExtra(RssFeed.class.getName()); 
        
        TextView titleView =(TextView) findViewById(R.id.rss_list_title);
        titleView.setText(rssFeed.getTitle());
        
        //get item from db
        rssItemList = DbRssItem.getAllItems(rssFeed.getId());
        
        itemListView=(ListView)findViewById(android.R.id.list);
       
        this.refreshList();
        
		this.addListenerOnButton();

    }

	void refreshList()
	{
        ItemListAdapter adapter = new ItemListAdapter(this, rssItemList);
        itemListView.setAdapter(adapter);        
        itemListView.setClickable(true);
        itemListView.setOnItemClickListener(this);    

	}
	
	
    private Thread mThread;  
    
    private Handler mHandler = new Handler() {  
        public void handleMessage (Message msg) {//姝ゆ柟娉曞湪ui绾跨▼杩愯  
            switch(msg.what) {  
            case MSG_SUCCESS:  
                //reload date from db
            	Log.i(tag, "Reload rss from db now");
            	 rssItemList = DbRssItem.getAllItems(rssFeed.getId());
            	 refreshList();            	
            	mThread = null;
                break;    
            case MSG_FAILURE:  
                Toast.makeText(getApplication(), getApplication().getString(R.string.get_rss_failure), Toast.LENGTH_LONG).show();  
                mThread = null;
                break;  
            }  
        }  
    }; 

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {		
		Log.i(tag, "item clicked! [" + rssItemList.get(arg2).getTitle()
				+ "]");
		//get the content from feed and parse the content
		RssItem rssItem = rssItemList.get(arg2);
		
		Intent itemintent = new Intent(this, ShowDetailActivity.class);

		Bundle bundle = new Bundle();
		bundle.putParcelable(RssItem.class.getName(), rssItem);  
		itemintent.putExtras(bundle);
        startActivity(itemintent);		
	}
	
	
	public void addListenerOnButton() {		 
		ImageButton imageButton = (ImageButton) findViewById(R.id.refresh);
 
		imageButton.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View arg0) { 
			  Log.i(tag, "refresh clicked"); 
              if(mThread == null) {  
                  mThread = new Thread(runnable);  
                  mThread.start();
              }  
              else {  
                  Toast.makeText(getApplication(), getApplication().getString(R.string.thread_started), Toast.LENGTH_LONG).show();  
              }  
			} 
		});
 
	}
	
    Runnable runnable = new Runnable() {  
        
        @Override  
        public void run() {
            try {

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet request = new HttpGet(rssFeed.getFeedUrl());
                HttpResponse  response = httpclient.execute(request);                   
                if(response.getStatusLine().getStatusCode() == 200){ 
                    HttpEntity entity = response.getEntity();
                    if (entity != null) {
                    	
                    	InputStream inputStream = entity.getContent();                   	

                    	FeedParser feedFarser = new FeedParser(rssFeed);
                        SAXParserFactory factory = SAXParserFactory.newInstance();
                        SAXParser parser = factory.newSAXParser();
                        XMLReader xmlreader = parser.getXMLReader();
                        xmlreader.setContentHandler(feedFarser);
                        
                        Reader reader = new InputStreamReader(inputStream,"UTF-8");                        
                        InputSource is = new InputSource(reader);
                        is.setEncoding("UTF-8");                       
                        xmlreader.parse(is);
                        
                        Log.i(tag, "end parse"); 
                        
                        List<RssItem> latestList = feedFarser.getRssFeed().getItemList();
                        
                        for(RssItem item : latestList)
                        {
                        	DbRssItem.addRssItem(item);
                        }
                    }                	
                }
            } catch (Exception e) {  
            	 Log.e("GVOA", "Connect or parse Error", e);
                mHandler.obtainMessage(MSG_FAILURE).sendToTarget();
                return;  
            }  
            mHandler.obtainMessage(MSG_SUCCESS).sendToTarget();
        }  
    }; 
	
 
}
