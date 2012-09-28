package com.medical.organizer.utilities;

import java.util.ArrayList;
import java.util.Random;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.medical.organizer.Main;
import com.medical.organizer.fragments.ContactListFragment;
import com.medical.organizer.fragments.PatientListFragment;
import com.medical.organizer.fragments.ScheduleListFragment;

public class Helper{
	public final static long FIFTEEN_MINUTES = 15*60*1000;
	
	//TESTING PURPOSES ONLY
	public final static long TWENTY_FIVE_SEC = 25*1000;
	
	public final static long FIVE_MINUTES = 5*60*1000;
	public final static long ONE_DAY = 24*60*60*1000;
	public final static int NORMAL = 0;
	public final static int UPDATE_SCHEDULE = 1;
	public final static int CHANGE_PATIENT_STATUS = 2;
	public final static int CHANGE_ALARM_STATUS = 3;
	private final static String PATIENTS = "Patients";
	private final static String SCHEDULE = "Schedule";
	private final static String CONTACTS = "Contacts";
	private final static String LOGIN = "Login";
	private Database db;
	
	public Helper(Context context)
	{
		db = new Database(context);
	}
	
	public void connectDatabase(){
		
		try {
			db.createDataBase();
			db.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Unable to create database");
		}
	}

	//GENERIC
	public ArrayList<Object> getData(Fragment f, String date)
	{
		ArrayList<Object> o = new ArrayList<Object>();
		Cursor c;
			if(f instanceof PatientListFragment && date == null)
			{
				o.clear();
				c = db.getData(PATIENTS, date);
				c.moveToFirst();
				int count = c.getCount();
					while(!c.isAfterLast())
					{
							Patients p = new Patients();
							p.setPatient_id(c.getString(c.getColumnIndex("_id")));
							p.setFname(c.getString(c.getColumnIndex("firstname")));
							p.setMi(c.getString(c.getColumnIndex("middlename")));
							p.setLname(c.getString(c.getColumnIndex("lastname")));
							p.setAddr(c.getString(c.getColumnIndex("address")));
							p.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
							p.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));
							p.setAge(c.getInt(c.getColumnIndex("age")));
							p.setMed_history(c.getString(c.getColumnIndex("med_history")));
							p.setPat_status(c.getInt(c.getColumnIndex("status")));
							o.add(p);
							c.moveToNext();
					}
				Log.d("dbcheck", PATIENTS+"-Number of Rows retrieved:: "+count);
			}
			
			if(f instanceof ScheduleListFragment && date != null)
			{
				Log.d("dbcheck", date);
				o.clear();
				c = db.getData(SCHEDULE, date);
				c.moveToFirst();
				int count = c.getCount();
					while(!c.isAfterLast())
					{
						Schedule s = new Schedule();
						s.setSchedule_id(c.getString(c.getColumnIndex("_id")));
						s.setPatient_id(c.getString(c.getColumnIndex("patient_id")));
						s.setTitle(c.getString(c.getColumnIndex("title")));
						s.setDescription(c.getString(c.getColumnIndex("desc")));
			    		s.setDate(c.getString(c.getColumnIndex("date")));
						s.setTime(c.getLong(c.getColumnIndex("time")));
						s.setType(c.getString(c.getColumnIndex("type")));
			    		s.setLocation(c.getString(c.getColumnIndex("location")));
			    		s.setRequestCode(c.getInt(c.getColumnIndex("request_code")));
			    		boolean flag = c.getInt(c.getColumnIndex("alarm")) == 1;
			    		s.setAlarm(flag);
			    		flag = c.getInt(c.getColumnIndex("alarm_status")) == 1;
			    		s.setDone(flag);
			    		o.add(s);
			    		c.moveToNext();
					}
				Log.d("dbcheck", SCHEDULE+"-Number of Rows retrieved:: "+count);
			}
			
			if(f instanceof ContactListFragment && date == null)
			{
				o.clear();
				c = db.getData(CONTACTS, date);
				c.moveToFirst();
				int count = c.getCount();
					while(!c.isAfterLast())
					{
						Contacts con = new Contacts();
						con.setC_id(c.getString(c.getColumnIndex("_id")));
						con.setAddr(c.getString(c.getColumnIndex("hosp_address")));
						con.setNum(c.getString(c.getColumnIndex("contact_number")));
						con.setName(c.getString(c.getColumnIndex("name")));
						con.setSpec(c.getString(c.getColumnIndex("specialty")));
						boolean flag = c.getInt(c.getColumnIndex("isImported")) == 1;
						con.setImported(flag);
						o.add(con);
						c.moveToNext();
					}
				Log.d("dbcheck", CONTACTS+"-Number of Rows retrieved:: "+count);
			}
			db.close();
		return o;
	}
		
	//UNIQUE TO PATINT-SCHEDULE
	public void getPatientSchedule(Object o)
	{
		Cursor c;
		if(o instanceof Patients)
		{
			Patients p = (Patients) o;
			c = db.getPatientScheduled(p.getPatient_id());
			c.moveToFirst();
			p.setSchedule_id(c.getString(c.getColumnIndex("_id")));
			p.setTime(c.getLong(c.getColumnIndex("time")));
			p.setLocation(c.getString(c.getColumnIndex("location")));
			p.setRequestCode(c.getInt(c.getColumnIndex("request_code")));
		}
		
		if(o instanceof Schedule)
		{
			Schedule s = (Schedule) o;
			c = db.getScheduledPatient(s.getPatient_id());
			c.moveToFirst();
			s.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
			s.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));

		}
		
		db.close();
	}
	
	public Patients getScheduledPatient(Schedule schedule)
	{
		Patients p = new Patients();
		
		Cursor c = db.getScheduledPatient(schedule.getPatient_id());
		c.moveToFirst();
				p.setPatient_id(c.getString(c.getColumnIndex("_id")));
				p.setFname(c.getString(c.getColumnIndex("firstname")));
				p.setMi(c.getString(c.getColumnIndex("middlename")));
				p.setLname(c.getString(c.getColumnIndex("lastname")));
				p.setAddr(c.getString(c.getColumnIndex("address")));
				p.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
				p.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));
				p.setAge(c.getInt(c.getColumnIndex("age")));
				p.setMed_history(c.getString(c.getColumnIndex("med_history")));
				p.setPat_status(c.getInt(c.getColumnIndex("status")));
		
		db.close();
		return p;
	}
	
	//UNIQUE TO SCHEDULE
	public Schedule getSchedule(String id)
	{
		Schedule s = null;
		
			Cursor c = db.getScheduleDetalils(id);
			if(c.getCount() > 0)
			{
				s = new Schedule();
				c.moveToFirst();
				s.setSchedule_id(c.getString(c.getColumnIndex("_id")));
				s.setPatient_id(c.getString(c.getColumnIndex("patient_id")));
				s.setTitle(c.getString(c.getColumnIndex("title")));
				s.setDescription(c.getString(c.getColumnIndex("desc")));
	    		s.setDate(c.getString(c.getColumnIndex("date")));
				s.setTime(c.getLong(c.getColumnIndex("time")));
				s.setType(c.getString(c.getColumnIndex("type")));
	    		s.setLocation(c.getString(c.getColumnIndex("location")));
	    		s.setRequestCode(c.getInt(c.getColumnIndex("request_code")));
	    		boolean flag = c.getInt(c.getColumnIndex("alarm")) == 1;
	    		s.setAlarm(flag);
	    		flag = c.getInt(c.getColumnIndex("alarm_status")) == 1;
	    		s.setDone(flag);
			}
    		db.close();
		return s;
	}
	
	
	//GENERIC
	public void insert(Object o)
	{
		Log.d("ClassCheck", "This is "+o.getClass().toString());
		Patients p = new Patients();
		Schedule s = new Schedule();
		Contacts c = new Contacts();
		ContentValues values = new ContentValues();
		if(o instanceof Patients)
		{	
			values.clear();
			p = (Patients) o;
			values.put("_id", p.getPatient_id());
			values.put("firstname", p.getFname());
			values.put("middlename", p.getMi());
	    	values.put("lastname", p.getLname());
	    	values.put("address", p.getAddr());
	    	values.put("age", p.getAge());
	    	values.put("med_history", p.getMed_history());
	    	values.put("hosp_name", p.getHosp_name());
	    	values.put("hosp_room", p.getHosp_room());
	    	values.put("status", p.getPat_status());
	    	db.pushData(PATIENTS, values);
		}
		if(o instanceof Schedule)
		{
			values.clear();
			s = (Schedule) o;
			values.put("_id", s.getSchedule_id());
			values.put("patient_id", s.getPatient_id());
			values.put("title", s.getTitle());
			values.put("time", s.getTime());
			values.put("location", s.getLocation());
			values.put("date", s.getDate());
	    	values.put("desc", s.getDescription());
	    	values.put("type", s.getType());
	    	values.put("alarm", s.isAlarm());
	    	values.put("request_code", s.getRequestCode());
	    	values.put("alarm_status", s.isDone());
	    	db.pushData(SCHEDULE, values);
		}
		if(o instanceof Contacts)
		{
			values.clear();
			c = (Contacts) o;
			values.put("_id", c.getC_id());
			values.put("name", c.getName());
	    	values.put("hosp_address", c.getAddr());
	    	values.put("contact_number", c.getNum());
	    	values.put("specialty", c.getSpec());
	    	values.put("isImported", c.isImported());
	    	db.pushData(CONTACTS, values);
		}

	}
	
	//GENERIC
	public void delete(Object o)
	{
		Patients p = new Patients();
		Schedule s = new Schedule();
		Contacts c = new Contacts();
		Log.d("ClassCheck", "This is "+o.getClass().toString());
		if(o instanceof Patients)
		{	
			p = (Patients) o;
			db.deleteRecord(PATIENTS, p.getPatient_id());
		}
		if(o instanceof Schedule)
		{
			s = (Schedule) o;
			db.deleteRecord(SCHEDULE, s.getSchedule_id());
		}
		if(o instanceof Contacts)
		{
			c = (Contacts) o;
			db.deleteRecord(CONTACTS, c.getC_id());
		}
	}
	
	//GENERIC
	public void update(Object o, int args)
	{
		ContentValues values = new ContentValues();
		switch (args) {
		case UPDATE_SCHEDULE:
			if(o instanceof Patients)
			{	
				Patients p = (Patients) o;
				values.clear();
				values.put("location", p.getLocation());
				values.put("time", p.getTime());
				db.updateSpecificId(values, SCHEDULE, p.getSchedule_id());
				
				values.clear();
				values.put("hosp_name", p.getHosp_name());
				values.put("hosp_room", p.getHosp_room());
				values.put("status", p.getPat_status());
				db.updateSpecificId(values, PATIENTS, p.getPatient_id());
				
			}
			
			if(o instanceof Schedule)
			{
				Schedule s = (Schedule) o;
				values.clear();
				values.put("location", s.getLocation());
				values.put("time", s.getTime());
				db.updateSpecificId(values, SCHEDULE, s.getSchedule_id());
				
				values.clear();
				values.put("hosp_name", s.getHosp_name());
				values.put("hosp_room", s.getHosp_room());
				db.updateSpecificId(values, PATIENTS, s.getPatient_id());
			}
			break;
		case CHANGE_PATIENT_STATUS:
			if(o instanceof Patients)
			{	
				Patients p = (Patients) o;
				values.clear();
				if(p.getPat_status() == 1)
				{
					values.put("hosp_name", p.getHosp_name());
					values.put("hosp_room", p.getHosp_room());
					values.put("status", p.getPat_status());
				}
					
				if(p.getPat_status() == 2)
					values.put("status", p.getPat_status());

				db.updateSpecificId(values, PATIENTS, p.getPatient_id());
			}
			break;
		case CHANGE_ALARM_STATUS:
			if(o instanceof Schedule)
			{	
				Schedule s = (Schedule) o;
				values.clear();
				values.put("alarm", s.isAlarm());
				values.put("alarm_status", s.isDone());
				values.put("request_code", s.getRequestCode());
				
					if(s.isAlarm())
						values.put("time", s.getTime());
					
				db.updateSpecificId(values, SCHEDULE, s.getSchedule_id());
			}
			break;
		case NORMAL:
				Patients p = new Patients();
				Schedule s = new Schedule();
				Contacts c = new Contacts();
		
				if(o instanceof Patients)
				{	
					Log.d("ClassCheck", "This is "+o.getClass().toString());
					values.clear();
					p = (Patients) o;
					values.put("firstname", p.getFname());
					values.put("middlename", p.getMi());
			    	values.put("lastname", p.getLname());
			    	values.put("address", p.getAddr());
			    	values.put("age", p.getAge());
			    	values.put("med_history", p.getMed_history());
			    	db.updateSpecificId(values, PATIENTS, p.getPatient_id());
			    	
				}
				if(o instanceof Schedule)
				{
					Log.d("ClassCheck", "This is "+o.getClass().toString());
					values.clear();
					s = (Schedule) o;
					values.put("title", s.getTitle());
					values.put("time", s.getTime());
					values.put("location", s.getLocation());
					values.put("date", s.getDate());
			    	values.put("desc", s.getDescription());
			    	values.put("type", s.getType());
			    	values.put("alarm", s.isAlarm());
			    	db.updateSpecificId(values, SCHEDULE, s.getSchedule_id());
				}
				if(o instanceof Contacts)
				{
					values.clear();
					c = (Contacts) o;
					values.put("_id", c.getC_id());
					values.put("name", c.getName());
			    	values.put("hosp_address", c.getAddr());
			    	values.put("contact_number", c.getNum());
			    	values.put("specialty", c.getSpec());
			    	values.put("isImported", c.isImported());
			    	db.updateSpecificId(values, CONTACTS, c.getC_id());
				}
			break;
			
		default:
			Log.d("HELPER ERROR", "INVALID ARGUMENTS on UPDATE");
			break;
		}
			
	}
	
	public boolean checkInputs(ArrayList<View> views)
	{
		int count = views.size();
		Log.d("viewGroupChildCount", "ViewGroup child Count: "+count);
		int countResult = 0;
		
		for(View item : views)
		{
			if(item instanceof EditText)
			{
				EditText eText = (EditText) item;
					if(!eText.getText().toString().equals("") && !(eText.getText().toString().trim().length() == 0) )
					{
						countResult++;
						Log.d("validation", "Length: "+eText.getText().toString().trim().length());
					}
			}
			
			if(item instanceof RadioGroup)
			{
				RadioGroup eRadioGroup = (RadioGroup) item;
				if(eRadioGroup.getCheckedRadioButtonId() != -1)
						countResult++;
			}
		}
		
		Log.d("viewGroupChildCount", "ViewGroup child Count that has Values: "+countResult);
		
		return countResult == count ? true : false;
		
	}
	
	public static int generateRequestCode(long timeInMills)
	{ 
		Random rand = new Random();
		int requestCode = rand.nextInt((int)((timeInMills/1000/60) / 1000));
		
			if(requestCode < 0)
				requestCode = rand.nextInt((int)((timeInMills/1000/60) / 1000));
			
		return requestCode;
	}
	
	public void registerLoginDetails(String deviceId, String email, String name)
	{
		ContentValues values = new ContentValues();
		values.put("device_id", deviceId);
		values.put("email", email);
		values.put("name", name);
		db.pushLoginDetails(values, LOGIN);
	}
	
	public boolean checkDeviceIdIfExist(String deviceId)
	{
		return db.checkDeviceId(deviceId);
	}
	
	public void scheduleAlarm(Context context,String id, int rCode, long triggerTime)
	{
		Log.d("RecieverCheck","Recieved ID: "+id);
		NotifyAlarm.TRIGGER_TIME = triggerTime;
		NotifyAlarm.CONTEXT = context;

		Intent intent = new Intent(Main.ACTION);
		intent.putExtra("schedule_id",id);
		
		NotifyAlarm.INTENT = intent;
		NotifyAlarm.startAlarm(rCode);
	}
	
	public void scheduleRepeatingAlarm(Context context,String id, int rCode, long triggerTime)
	{
	
		NotifyAlarm.TRIGGER_TIME = triggerTime;
		NotifyAlarm.CONTEXT = context;
		
		Intent intent = new Intent(Main.ACTION);
		intent.putExtra("schedule_id",id);
		
		NotifyAlarm.INTENT = intent;
		NotifyAlarm.startAlarmRepeating(rCode, Helper.TWENTY_FIVE_SEC);
	}

	public void cancelAlarm(Context context, int requestCode)
	{
		NotifyAlarm.CONTEXT = context;
		Intent intent = new Intent(Main.ACTION);
		NotifyAlarm.INTENT = intent;
		NotifyAlarm.stopAlarm(requestCode);
	}
	//FOR CLEANING ONLY DEV's ONLY
	//public void clear(String table)
	//{
	//	db.clear(table);
	//}
	//public void clearAll()
	//{
	//	db.clear(PATIENTS);
	//	db.clear(SCHEDULE);
	//	//db.clear(CONTACTS);
	//}
	
}
