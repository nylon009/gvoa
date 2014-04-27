package com.gapp.gvoa.ui;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gapp.gvoa.R;
import com.gapp.gvoa.datatype.RssItem;

public class ItemListAdapter extends ArrayAdapter<RssItem> {
	
	private final Context context;
	private final List<RssItem> rssItemList; 
	

	public ItemListAdapter(Context context, List<RssItem> objects) {
		super(context,R.layout.rss_item_row, objects);
		this.context = context; 
		this.rssItemList = objects;

	}
	
	
	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.rss_item_row, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    
	    RssItem item = rssItemList.get(position);
	    textView.setText(item.getTitle() );

	    if (item.getLocalmp3()!=null) {
	      imageView.setImageResource(R.drawable.emo_im_laughing);
	    } else {
	      imageView.setImageResource(R.drawable.emo_im_angel);
	    }
	    return rowView;
	  }


}