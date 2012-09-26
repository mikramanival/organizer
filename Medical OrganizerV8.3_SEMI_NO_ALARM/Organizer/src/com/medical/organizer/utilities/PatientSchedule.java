package com.medical.organizer.utilities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PatientSchedule implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String patient_id;
	private String schedule_id;
	private String hosp_room;
	private String hosp_name;
	private long time;
	private String location;
	private int requestCode;
	

	public String getPatient_id() {
		return patient_id;
	}
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}
	public String getSchedule_id() {
		return schedule_id;
	}
	public void setSchedule_id(String schedule_id) {
		this.schedule_id = schedule_id;
	}
	public String getHosp_room() {
		return hosp_room;
	}
	public void setHosp_room(String hosp_room) {
		this.hosp_room = hosp_room;
	}
	public String getHosp_name() {
		return hosp_name;
	}
	public void setHosp_name(String hosp_name) {
		this.hosp_name = hosp_name;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public int getRequestCode() {
		return requestCode;
	}
	public void setRequestCode(int requestCode) {
		this.requestCode = requestCode;
	}
	
	public static String formatDate(long millisec,String dateTimeFormat)
	{
		SimpleDateFormat format = new SimpleDateFormat(dateTimeFormat);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millisec);
		return format.format(cal.getTime());
	}
	
	public String time()
	{
		return formatDate(getTime(),"hh:mm a");
	}
}
