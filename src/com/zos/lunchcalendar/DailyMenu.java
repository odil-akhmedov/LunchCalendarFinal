package com.zos.lunchcalendar;

import java.sql.Date;

import android.R.string;
import android.text.format.DateFormat;

public class DailyMenu {
	public String title;
	public String startTime;
	public String endTime;
	
	/*public String id;
	public String authorName;	
	public String authorEmail;*/

	 public DailyMenu(){
		 
		 this.title = "Food";
		 this.startTime = "2000-01-01";
		 this.endTime = "2000-01-01";
		 
		 /*this.id = "";
		 this.authorName = "John Doe";
		 this.authorEmail = "john@doe.com";*/		 
	 }
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public String getEndTime() {
		return startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	 public DailyMenu(String title, String startTime, String endTime) {
	        super();
	        this.title = title;
	        this.startTime = startTime;
	        this.endTime = endTime;
	    }

	
	
}
