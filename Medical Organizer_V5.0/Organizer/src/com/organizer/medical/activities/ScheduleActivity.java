package com.organizer.medical.activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.organizer.medical.others.Schedule;
import com.organizer.medical.others.ScheduleAdapter;

public class ScheduleActivity extends Activity {
	private final static String TABLE_NAME = "Schedule";

	Date curr;
	boolean flag_alarm;
	ArrayList<Schedule> sched_list;
	final MedicalHelper dbHelper = new MedicalHelper(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_schedule);
       final CalendarView cw = (CalendarView) findViewById(R.id.schedule_calendar);
       cw.setOnDateChangeListener(new OnDateChangeListener() {
		
		public void onSelectedDayChange(CalendarView view, int year, int month,
				int dayOfMonth) {
			Dialog open_event_list = new Dialog(ScheduleActivity.this);
			open_event_list.setContentView(R.layout.event_list);
			open_event_list.setTitle("Events on "+formatDate(cw.getDate()));
			ImageButton add_event_button = (ImageButton) open_event_list.findViewById(R.id.add_event);
			final ListView event_list = (ListView) open_event_list.findViewById(R.id.events_list);
			curr = new Date();
			loadList(cw.getDate(), event_list);
					//set listener to open add_event layout
					add_event_button.setOnClickListener(new OnClickListener() {					
						public void onClick(View v) {
							if(curr.getTime() <= cw.getDate() || formatDate(cw.getDate()).equals(formatDate(curr.getTime())))
							{
									final Dialog open_add_event = new Dialog(ScheduleActivity.this);
									open_add_event.setContentView(R.layout.add_event);
									open_add_event.setTitle("Set Today ("+formatDate(cw.getDate())+")");
									Time time = new Time();
									final ToggleButton toggle_alarm = (ToggleButton) open_add_event.findViewById(R.id.toggle_alarm);
									
									//EditText
									final EditText title = (EditText) open_add_event.findViewById(R.id.title);
									final EditText desc = (EditText) open_add_event.findViewById(R.id.desc);
									final EditText loc = (EditText) open_add_event.findViewById(R.id.location);
									final RadioButton re = (RadioButton) open_add_event.findViewById(R.id.rem);
									final RadioButton eve = (RadioButton) open_add_event.findViewById(R.id.eve);
									
									//Timepicker
									final TimePicker timePicker = (TimePicker) open_add_event.findViewById(R.id.time_picker);

									//TOGGLE BUTTON
									toggle_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
										
										public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
											if(isChecked)
											{
												flag_alarm = isChecked;
											}
											else
											{
												flag_alarm = isChecked;
											}
											
										}
									});//END of toggle_alarm
									
									Button save_event_button = (Button) open_add_event.findViewById(R.id.save_event);
									//SAVE BUTTON LISTENER
										save_event_button.setOnClickListener(new OnClickListener() {
											
											public void onClick(View v) {
												Time time = new Time();
												final Schedule s = new Schedule();
												time.hour = timePicker.getCurrentHour();
												time.minute = timePicker.getCurrentMinute();
												long saveTime = time.toMillis(false);
												
												s.setTitle(title.getText().toString());
												s.setDescription(desc.getText().toString());
												s.setLocation(loc.getText().toString());
												s.setDate(cw.getDate());
												s.setTime(saveTime);
												if(re.isChecked())
													s.setType("r");
												
												if(eve.isChecked())
													s.setType("e");
												
												s.setAlarm(flag_alarm);
												setEventIntoDatabase(s);
												open_add_event.cancel();
												loadList(cw.getDate(), event_list);
												Toast toast = Toast.makeText(ScheduleActivity.this, "Event Set!", Toast.LENGTH_SHORT);
												toast.show();
											}
										}); //END OF SAVE BUTTON LISTENER
									
									open_add_event.show();
							}
							else
							{
								Toast toast = Toast.makeText(ScheduleActivity.this, "Can't add Events before Current Date/Time", Toast.LENGTH_SHORT);
								toast.show();
							}
						}
					});//END of add_event_button Listener (opens add_event.xml)
						
				open_event_list.show();
		}
	});//END of CalendarView Listener (opens event_list.xml)
       
    }//END OF onCreate();

    
    
    
    
    public void loadList(long cwdate, ListView lv)
    {
    	Cursor c = dbHelper.retrieveAllEventsWhere(TABLE_NAME, cwdate);
    	sched_list = new ArrayList<Schedule>();
    	Schedule s;
    	c.moveToFirst();
    	while(!c.isAfterLast())
    	{
    		s = new Schedule();
    		s.setTitle(c.getString(c.getColumnIndex("title")));
    		s.setDescription(c.getString(c.getColumnIndex("desc")));
    		s.setTime(c.getLong(c.getColumnIndex("time")));
    		if(c.getInt(c.getColumnIndex("alarm")) == 1)
    			s.setAlarm(true);
    		else
    			s.setAlarm(false);
    		Log.d("dbcheck", s.toString());
    		sched_list.add(s);
    		c.moveToNext();
    	}
    	dbHelper.close();
    	Log.d("dbcheck", "==============================");
    	for(Schedule sc: sched_list)
    	{
    		Log.d("dbcheck", sc.toString());
    	}
    	ScheduleAdapter schedListAdapter = new ScheduleAdapter(this, R.layout.schedule_list_item, sched_list);
    	lv.setAdapter(schedListAdapter);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_schedule, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	if(item.getItemId() == R.id.add_event)
    	{
    		Dialog dialog = new Dialog(ScheduleActivity.this);
    		dialog.setTitle("Add Event On..");
    		
    		dialog.show();
    	}
    	return super.onOptionsItemSelected(item);
    }

    public String formatDate(long millisec)
    {
    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millisec);
    	return format.format(cal.getTime());
    }
    
    public void setEventIntoDatabase(Schedule s)
    {
    	ContentValues insertIntoDatabase = new ContentValues();
    	
    	insertIntoDatabase.put("title", s.getTitle());
    	insertIntoDatabase.put("desc", s.getDescription());
    	insertIntoDatabase.put("type", s.getType());
    	insertIntoDatabase.put("time", s.getTime());
    	insertIntoDatabase.put("date", s.getDate());
    	insertIntoDatabase.put("alarm", s.isAlarm());
    	insertIntoDatabase.put("location", s.getLocation());
    	
    	dbHelper.insertIntoDatabase(TABLE_NAME, insertIntoDatabase);
    }
    
}
