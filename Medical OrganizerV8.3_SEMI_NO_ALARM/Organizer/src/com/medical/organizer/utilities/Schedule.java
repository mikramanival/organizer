package com.medical.organizer.utilities;


public class Schedule extends PatientSchedule{

/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String title;
private String description;
private String type;
private long time;
private String date;
private boolean alarm;
private boolean done;

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
public long getTime() {
	return time;
}
public void setTime(long time) {
	this.time = time;
}
public String getDate() {
	return date;
}
public void setDate(String date) {
	this.date = date;
}
public boolean isAlarm() {
	return alarm;
}
public void setAlarm(boolean alarm) {
	this.alarm = alarm;
}
public boolean isDone() {
	return done;
}
public void setDone(boolean done) {
	this.done = done;
}
public String toString()
{
	return formatDate(getTime(),"hh:mm a")+" | "+getTitle();
}




}
