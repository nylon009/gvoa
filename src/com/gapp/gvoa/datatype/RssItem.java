package com.gapp.gvoa.datatype;

import android.os.Parcel;
import android.os.Parcelable;

public class RssItem implements Parcelable {
    private Integer id = 0;
	private Integer feedID = null;
    private String title = null;
	private String pubDate = null;
	private String link = null;
	private String description = null;
    private String fullText = null;    
    
	public RssItem() {
		
	}

	public RssItem(Integer id, Integer feedID, String title, String sdate,
			String link, String description,  String fullText) {
		super();
		this.id = id;
		this.feedID = feedID;
		this.title = title;
		this.pubDate = sdate;
		this.link = link;
		this.description = description;
		this.fullText = fullText;
	}
	
    public static final Parcelable.Creator<RssItem> CREATOR = new Creator<RssItem>() {  
        public RssItem createFromParcel(Parcel source) {  
        	RssItem rssItem = new RssItem(source);  
            return rssItem;  
        }  
        public RssItem[] newArray(int size) {  
            return new RssItem[size];  
        }  
    };  
      
	
	private RssItem(Parcel in) {
		id = in.readInt();
		feedID = in.readInt();
		title = in.readString();
		pubDate = in.readString();
		link = in.readString();
		description = in.readString();
		fullText = in.readString();	
    }
	

	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getFullText() {
		return fullText;
	}
	public void setFullText(String fullText) {
		this.fullText = fullText;
	}
	public String getPubDate() {
		return pubDate;
	}
	public void setPubDate(String sdate) {
		this.pubDate = sdate;
	}
	public Integer getFeedID() {
		return feedID;
	}
	public void setFeedID(Integer feedID) {
		this.feedID = feedID;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}

	public String toString()
	{
		return title;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(feedID);
		dest.writeString(title);
		dest.writeString(pubDate);
		dest.writeString(link);
		dest.writeString(description);
		dest.writeString(fullText);		
	}
	
}
