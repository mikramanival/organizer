package com.medical.organizer.utilities;

import java.util.ArrayList;
import java.util.HashMap;

import com.medical.organizer.fragments.PatientListFragment;
import com.medical.organizer.fragments.ScheduleFragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class Helper{
	private final static String PATIENTS = "Patients";
	private final static String SCHEDULE = "Schedule";
	private final static String CONTACTS = "Contacts";
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
	//UNIQUE TO PATINT-SCHEDULE
	/*public ArrayList<Patients> getPatientData()
	{
		ArrayList<Patients> list = new ArrayList<Patients>();
		Cursor c = db.getData(PATIENTS);
		c.moveToFirst();
		int count = c.getCount();
			while(!c.isAfterLast())
			{
					Patients p = new Patients();
					p.setId(c.getString(c.getColumnIndex("_id")));
					p.setFname(c.getString(c.getColumnIndex("firstname")));
					p.setMi(c.getString(c.getColumnIndex("middlename")));
					p.setLname(c.getString(c.getColumnIndex("lastname")));
					p.setAddr(c.getString(c.getColumnIndex("address")));
					p.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
					p.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));
					p.setAge(c.getInt(c.getColumnIndex("age")));
					p.setMed_history(c.getString(c.getColumnIndex("med_history")));
					p.setPat_status(c.getInt(c.getColumnIndex("status")));
					list.add(p);
				   	c.moveToNext();
			}
		db.close();
			//for(Patients p: list)
			//{
		Log.d("dbcheck", PATIENTS+"-Number of Rows retrieved:: "+count);
			//}
		return list;
	}*/
	
	//GENERIC
	public ArrayList<Object> getData(Fragment f)
	{
		ArrayList<Object> o = new ArrayList<Object>();
		Cursor c;
			if(f instanceof PatientListFragment)
			{
				c = db.getData(PATIENTS);
				c.moveToFirst();
				int count = c.getCount();
					while(!c.isAfterLast())
					{
							Patients p = new Patients();
							p.setId(c.getString(c.getColumnIndex("_id")));
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
			
			if(f instanceof ScheduleFragment)
			{
				
			}
				
			db.close();
		return o;
	}
		
	//UNIQUE TO PATINT-SCHEDULE
	public Schedule getPatientSchedule(String pat_id)
	{
		Schedule s = new Schedule();
		Cursor c = db.getPatientSchedule(pat_id);
		c.moveToFirst();
		s.setId(c.getInt(c.getColumnIndex("_id")));
		s.setPatient_id(c.getString(c.getColumnIndex("patient_id")));
		s.setTitle(c.getString(c.getColumnIndex("title")));
		s.setDescription(c.getString(c.getColumnIndex("desc")));
		s.setDate(c.getString(c.getColumnIndex("date")));
		s.setTime(c.getLong(c.getColumnIndex("time")));
		s.setType(c.getString(c.getColumnIndex("type")));
		s.setLocation(c.getString(c.getColumnIndex("location")));
		boolean flag = c.getInt(c.getColumnIndex("alarm")) == 1;
		s.setAlarm(flag);
		db.close();
		return s;
	}
	
	/*public Object getRecord(String table_name, String id)
	{
		Object obj = null;
		Cursor c = db.getRecord(table_name, id);
		c.moveToFirst();
		if(c.getCount() > 0)
		{
			if(table_name.equals(PATIENTS))
			{
				Patients p = new Patients();
				p.setId(c.getString(c.getColumnIndex("_id")));
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
				obj = p;
			}
			if(table_name.equals(SCHEDULE))
			{
				Schedule s = new Schedule();
				s.setId(c.getInt(c.getColumnIndex("_id")));
				s.setTitle(c.getString(c.getColumnIndex("title")));
				s.setDescription(c.getString(c.getColumnIndex("desc")));
				s.setDate(c.getString(c.getColumnIndex("date")));
				s.setTime(c.getLong(c.getColumnIndex("time")));
				s.setType(c.getString(c.getColumnIndex("type")));
				s.setLocation(c.getString(c.getColumnIndex("location")));
				if(c.getInt(c.getColumnIndex("alarm")) == 1)
					s.setAlarm(true);
				else
					s.setAlarm(false);
				db.close();
				obj = s;
			}
			if(table_name.equals(CONTACTS))
			{
				Contacts con = new Contacts();
	
				
				db.close();
				obj = con;
			}
			
		}
		
		return obj;
	}
	*/
	
	//GENERIC
	public void insert(Object o)
	{
		Log.d("ClassCheck", "This is "+o.getClass().toString());
		Patients p = new Patients();
		Schedule s = new Schedule();
		//Contacts c = new Contacts();
		ContentValues values = new ContentValues();
		if(o instanceof Patients)
		{	
			values.clear();
			p = (Patients) o;
			values.put("_id", p.getId());
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
			values.put("patient_id", s.getPatient_id());
			values.put("title", s.getTitle());
			values.put("time", s.getTime());
			values.put("location", s.getLocation());
			values.put("date", s.getDate());
	    	values.put("desc", s.getDescription());
	    	values.put("type", s.getType());
	    	values.put("alarm", s.isAlarm());
	    	db.pushData(SCHEDULE, values);
		}
		if(o instanceof Contacts)
		{
			values.clear();
			//c = (Contacts) o;
		}

	}
	
	//GENERIC
	public void delete(Object o)
	{
		Patients p = new Patients();
		Schedule s = new Schedule();
		//Contacts c = new Contacts();
		Log.d("ClassCheck", "This is "+o.getClass().toString());
		if(o instanceof Patients)
		{	
			p = (Patients) o;
			db.deleteRecord(PATIENTS, p.getId());
		}
		if(o instanceof Schedule)
		{
			s = (Schedule) o;
			db.deleteRecord(SCHEDULE, s.getId());
		}
		if(o instanceof Contacts)
		{
			
		}
	}
	
	//GENERIC
	public void update(Object o)
	{
		
		Patients p = new Patients();
		Schedule s = new Schedule();
		//Contacts c = new Contacts();
		ContentValues values = new ContentValues();
		if(o instanceof Patients)
		{	
			Log.d("ClassCheck", "This is "+o.getClass().toString());
			values.clear();
			p = (Patients) o;
			values.put("_id", p.getId());
			values.put("firstname", p.getFname());
			values.put("middlename", p.getMi());
	    	values.put("lastname", p.getLname());
	    	values.put("address", p.getAddr());
	    	values.put("age", p.getAge());
	    	values.put("med_history", p.getMed_history());
	    	values.put("hosp_name", p.getHosp_name());
	    	values.put("hosp_room", p.getHosp_room());
	    	values.put("status", p.getPat_status());
	    	db.updateSpecificId(values, PATIENTS, p.getId());
	    	
		}
		if(o instanceof Schedule)
		{
			Log.d("ClassCheck", "This is "+o.getClass().toString());
			values.clear();
			s = (Schedule) o;
			values.put("patient_id", s.getPatient_id());
			values.put("title", s.getTitle());
			values.put("time", s.getTime());
			values.put("location", s.getLocation());
			values.put("date", s.getDate());
	    	values.put("desc", s.getDescription());
	    	values.put("type", s.getType());
	    	values.put("alarm", s.isAlarm());
	    	db.updateSpecificId(values, SCHEDULE, s.getId());
		}
		if(o instanceof Contacts)
		{
			values.clear();
			//c = (Contacts) o;
		}
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
		//db.clear(CONTACTS);
	//}
	

}
