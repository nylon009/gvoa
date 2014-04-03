package com.gapp.gvoa.datatype;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RssFeed implements Parcelable{
	
	private Integer id =-1;
	private String title;	
	private String feedUrl;
	private String feedDescription;
		
	private ArrayList<RssItem> itemList= new ArrayList<RssItem>();

	public RssFeed(Integer id, String title, String feedUrl,
			String feedDescription) {
		super();
		this.setId(id);
		this.setTitle(title);
		this.feedUrl = feedUrl;
		this.feedDescription = feedDescription;
	}

	
    public static final Parcelable.Creator<RssFeed> CREATOR = new Creator<RssFeed>() {  
        public RssFeed createFromParcel(Parcel source) {  
        	RssFeed rssFeed = new RssFeed(source);  
            return rssFeed;  
        }  
        public RssFeed[] newArray(int size) {  
            return new RssFeed[size];  
        }  
    };  
      
	
	private RssFeed(Parcel in) {
		id = in.readInt();
		title = in.readString();
		feedUrl = in.readString();
		feedDescription = in.readString();		
    }

	
	public String getFeedDescription() {
		return feedDescription;
	}
	
	public void setFeedDescription(String feedDescription) {
		this.feedDescription = feedDescription;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}



	public String getTitle() {
		return title;
	}

	
	public void addItem(RssItem item)
	{
		itemList.add(item);
	}


	public String getFeedUrl() {
		return feedUrl;
	}



	public void setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
	}



	public ArrayList<RssItem> getItemList() {
		return itemList;
	}



	public void setTitle(String title) {
		this.title = title;
	}

	public String toString()
	{
		/*if (title.length() > 42)
		{
			return title.substring(0, 42) + "...";
		}*/
		return title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		parcel.writeInt(id);
		parcel.writeString(title);
		parcel.writeString(feedUrl);
		parcel.writeString(feedDescription);
	}
}
