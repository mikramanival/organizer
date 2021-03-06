package com.organizer.medical.others;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Schedule {
private int id;
private String title;
private String description;
private String type;
private String location;
private long time;
private long date;
private boolean alarm;

public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
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
public String getType() {
	return type;
}
public void setType(String type) {
	this.type = type;
}
public String getLocation() {
	return location;
}
public void setLocation(String location) {
	this.location = location;
}
public long getTime() {
	return time;
}
public void setTime(long time) {
	this.time = time;
}
public long getDate() {
	return date;
}
public void setDate(long date) {
	this.date = date;
}
public boolean isAlarm() {
	return alarm;
}
public void setAlarm(boolean alarm) {
	this.alarm = alarm;
}
private String formatDate(long millisec,String dateTimeFormat)
{
	SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
	Calendar cal = Calendar.getInstance();
	cal.setTimeInMillis(millisec);
	return format.format(cal.getTime());
}
public String toString()
{
	return formatDate(getTime(),"hh:mm")+" | "+getTitle();
}

}
