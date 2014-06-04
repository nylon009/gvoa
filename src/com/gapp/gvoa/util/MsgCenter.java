package com.gapp.gvoa.util;

import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Message;

public class MsgCenter {

	public final String tag = "MsgCenter";
	public enum MsgType{
		E_NETWORK,
		E_RSSITEM_UPDATE
	}	
	
	private static MsgCenter _instance=null; 
	
	
	private boolean isRun;
	private BlockingQueue<Message> msgQueue = new LinkedBlockingQueue<Message>();
	
	private LinkedList<GSubscriber>  subScriberList = new LinkedList<GSubscriber>();
	
	private Thread msgThread = null; 
	
	
	
    public interface GSubscriber    {
    	public void onMessage(Message msg);
    }

	public static MsgCenter instance(){
		synchronized(MsgCenter.class){
			if(null==_instance)			{
				_instance= new MsgCenter();
				_instance.init();
			}
		}
		
		return _instance; 
	}
	
	private void init(){
		isRun = true;
		msgThread = new Thread(new MessageRunnable()); 
		msgThread.start();
	}
	
	
	
	public void stop()	{
		isRun = false; 
		try {
			if(null!=msgThread){
	     		msgThread.interrupt();
				msgThread.join();
				msgThread=null;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
    private class MessageRunnable implements Runnable {

        public void run() {
        	while (isRun) {
        	    try {			      
			    	  Message msg = msgQueue.take();
			          distributeMsg(msg);			     
			    } catch (InterruptedException ex) {
			    	ex.printStackTrace();
			    }
             }
        }
    }
    
    private void distributeMsg(Message msg)    {
    	for (GSubscriber sub : subScriberList)
    	{
    		sub.onMessage(msg);
    	}
    }
    
    public void postMessage(Message msg)    {
    	try {
			msgQueue.put(msg);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }    
    


    public void register(GSubscriber subscriber)    {	    
    	subScriberList.add(subscriber);    
    }
    
    public void unRegister(GSubscriber subscriber)    {	    
    	subScriberList.remove(subscriber);    
    } 
	
	
}
