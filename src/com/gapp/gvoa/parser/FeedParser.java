package com.gapp.gvoa.parser;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

import com.gapp.gvoa.datatype.RssFeed;
import com.gapp.gvoa.datatype.RssItem;

public class FeedParser extends DefaultHandler {
	public static final String tag = "FeedParser";
	enum ParseType{
		E_FEED,
		E_ITEM
	}
	
    private RssFeed rssFeed = null;
	private RssItem  currentRssItem = null;
    private StringBuilder builder = null;
	private String elementName = "";
	private ParseType parseType;

	
	public FeedParser(RssFeed rssFeed2) {
		rssFeed = rssFeed2;
	}
	
	public RssFeed getRssFeed(){
		return rssFeed;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
        elementName = qName;
        Log.i(tag, "startElement "+elementName);
		if(elementName.equalsIgnoreCase("channel")){			
			parseType = ParseType.E_FEED;
		}
		else if(elementName.equalsIgnoreCase("item")){
			parseType = ParseType.E_ITEM;
			currentRssItem = new RssItem();
		}
		
		builder = new StringBuilder();		
		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if(builder != null)
		{
			builder.append(ch,start,length);
		}		
		super.characters(ch, start, length);		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		elementName = qName; 
		
		if(elementName.equalsIgnoreCase("item")){
			Log.i(tag, "add Item"+currentRssItem);
			parseType = ParseType.E_FEED;
			currentRssItem.setFeedID(rssFeed.getId());
			rssFeed.addItem(currentRssItem);
			currentRssItem = null;
		}
		
		String value = null;
		if(null!= builder)
		{
			value = builder.toString();
		}
		Log.i(tag, "end Element "+elementName+" with "+value );
        
		if(elementName.equalsIgnoreCase("pubDate")){
		    if(ParseType.E_ITEM == parseType){
		    	currentRssItem.setPubDate(value);
		    }
		    else if(ParseType.E_FEED == parseType)
		    {
		    	//
		    }
		}
		else if(elementName.equalsIgnoreCase("title")){
		    if(ParseType.E_ITEM == parseType){
		    	currentRssItem.setTitle(value);
		    }
		}
		else if(elementName.equalsIgnoreCase("link")){
		    if(ParseType.E_ITEM == parseType){
		    	currentRssItem.setLink(value);
		    }
		}
		else if(elementName.equalsIgnoreCase("description")){
		    if(ParseType.E_ITEM == parseType){
		    	currentRssItem.setDescription(value);
		    }
		}	
		super.endElement(uri, localName, qName);
	}

}
