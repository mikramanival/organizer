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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
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
	private ImageButton prevButton=null;
	private ListView event_list;
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
			event_list = (ListView) open_event_list.findViewById(R.id.events_list);
			curr = new Date();
			loadList(cw.getDate());
					//set listener to open add_event layout
					add_event_button.setOnClickListener(new OnClickListener() {					
						public void onClick(View v) {
							if(curr.getTime() <= cw.getDate() || formatDate(cw.getDate()).equals(formatDate(curr.getTime())))
							{
									final Dialog open_add_event = new Dialog(ScheduleActivity.this);
									open_add_event.setContentView(R.layout.add_update_event);
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
									final ImageView alarm_icon = (ImageView) open_add_event.findViewById(R.id.imageView1);
									//TOGGLE BUTTON
									toggle_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
										
										public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
											if(isChecked)
											{
												flag_alarm = isChecked;
												alarm_icon.setImageResource(R.drawable.alarm_check);
											}
											else
											{
												flag_alarm = isChecked;
												alarm_icon.setImageResource(R.drawable.alarm_x);
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
												loadList(cw.getDate());
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

    
    
    
    
    public void loadList(final long cwdate)
    {
    	Cursor c = dbHelper.retrieveAllEventsWhere(TABLE_NAME, cwdate);
    	sched_list = new ArrayList<Schedule>();
    	Schedule s;
    	c.moveToFirst();
    	while(!c.isAfterLast())
    	{
    		s = new Schedule();
    		s.setId(c.getInt(c.getColumnIndex("_id")));
    		s.setTitle(c.getString(c.getColumnIndex("title")));
    		s.setDescription(c.getString(c.getColumnIndex("desc")));
    		//s.setDate(c.getLong(c.getColumnIndex("date")));
    		s.setTime(c.getLong(c.getColumnIndex("time")));
    		s.setType(c.getString(c.getColumnIndex("type")));
    		//s.setLocation(c.getString(c.getColumnIndex("location")));
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
    	event_list.setAdapter(schedListAdapter);

    	event_list.setOnItemLongClickListener(new OnItemLongClickListener() {

 			public boolean onItemLongClick(AdapterView<?> arg0, View v,
 					int arg2, long arg3) {
 				
 				FrameLayout fl = (FrameLayout) v.findViewById(R.id.sched_list_item);
 				ImageButton image_button = (ImageButton) fl.findViewById(R.id.delete_sched);
 				
 				final TextView hidden_id = (TextView) v.findViewById(R.id.sched_id);
 				
 				if(prevButton != null)
 					prevButton.setVisibility(Button.GONE);
 				
 				prevButton = image_button;
 				
 				image_button.setVisibility(Button.VISIBLE);
 				
 					image_button.setOnClickListener(new OnClickListener() {
 						
 						public void onClick(View v) {
 							
 							dbHelper.deleteRecordById(TABLE_NAME, Integer.parseInt(hidden_id.getText().toString()));
 							loadList(cwdate);
 							
 							Toast toast = Toast.makeText(ScheduleActivity.this, "Deleted!", 5000);
 							toast.show();
 						}
 					});
 				
 				return false;
 			}	
 	    });
    	
    	event_list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				if(prevButton != null)
 					prevButton.setVisibility(Button.GONE);
				
				final TextView sched_id = (TextView) v.findViewById(R.id.sched_id);
				
				Schedule s = new Schedule();
				Cursor c = dbHelper.retrieveSpecificId(TABLE_NAME,Integer.parseInt(sched_id.getText().toString()));
				c.moveToFirst();
				
				s.setId(c.getInt(c.getColumnIndex("_id")));
	    		s.setTitle(c.getString(c.getColumnIndex("title")));
	    		s.setDescription(c.getString(c.getColumnIndex("desc")));
	    		s.setDate(c.getLong(c.getColumnIndex("date")));
	    		s.setTime(c.getLong(c.getColumnIndex("time")));
	    		s.setType(c.getString(c.getColumnIndex("type")));
	    		s.setLocation(c.getString(c.getColumnIndex("location")));
	    		if(c.getInt(c.getColumnIndex("alarm")) == 1)
	    			s.setAlarm(true);
	    		else
	    			s.setAlarm(false);
	    		Log.d("gotFromDb", s.toString());
				
				dbHelper.close();
				final Dialog edit_dialog = new Dialog(ScheduleActivity.this);
				edit_dialog.setTitle("Edit Event on "+formatDate(cwdate));
				edit_dialog.setContentView(R.layout.add_update_event);
					Switch make_editable = (Switch) edit_dialog.findViewById(R.id.make_editable);
						make_editable.setVisibility(Switch.VISIBLE);
					final TimePicker timePicker = (TimePicker) edit_dialog.findViewById(R.id.time_picker);	
						Time time = new Time();
						time.set(s.getTime());
						timePicker.setCurrentHour(time.hour);
						timePicker.setCurrentMinute(time.minute);
					final EditText title = (EditText) edit_dialog.findViewById(R.id.title);		
						title.setText(s.getTitle());
					
					final EditText desc = (EditText) edit_dialog.findViewById(R.id.desc);		
						desc.setText(s.getDescription());
					
					final EditText loc = (EditText) edit_dialog.findViewById(R.id.location);		
						loc.setText(s.getLocation());
					final ImageView alarm_image = (ImageView) edit_dialog.findViewById(R.id.imageView1);	
					
					final ToggleButton alarm = (ToggleButton) edit_dialog.findViewById(R.id.toggle_alarm);	
					final ImageView alarm_icon = (ImageView) edit_dialog.findViewById(R.id.imageView1);
					if(s.isAlarm())
					{
						alarm.setChecked(s.isAlarm());
						alarm_icon.setImageResource(R.drawable.alarm_check);
					}
					else
					{
						alarm.setChecked(s.isAlarm());
						alarm_icon.setImageResource(R.drawable.alarm_x);
					}
					
					final RadioButton r = (RadioButton) edit_dialog.findViewById(R.id.rem);
					final RadioButton e = (RadioButton) edit_dialog.findViewById(R.id.eve);
					
					if(s.getType().equals("r"))
						r.setChecked(true);
					
					if(s.getType().equals("e"))
						e.setChecked(true);
					final Button save_event = (Button) edit_dialog.findViewById(R.id.save_event);
					
					timePicker.setEnabled(false);
					title.setEnabled(false);
					desc.setEnabled(false);
					loc.setEnabled(false);
					alarm.setEnabled(false);
					r.setEnabled(false);
					e.setEnabled(false);
					save_event.setEnabled(false);
					alarm_image.setVisibility(ImageView.INVISIBLE);	
					
					
					
					make_editable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if(isChecked)
							{
								timePicker.setEnabled(true);
								title.setEnabled(true);
								desc.setEnabled(true);
								loc.setEnabled(true);
								alarm.setEnabled(true);
								r.setEnabled(true);
								e.setEnabled(true);
								save_event.setEnabled(true);
								alarm_image.setVisibility(ImageView.VISIBLE);
								
							}
							else
							{
								timePicker.setEnabled(false);
								title.setEnabled(false);
								desc.setEnabled(false);
								loc.setEnabled(false);
								alarm.setEnabled(false);
								r.setEnabled(false);
								e.setEnabled(false);
								save_event.setEnabled(false);
								alarm_image.setVisibility(ImageView.INVISIBLE);								
							}
							
						}
					});
					
					alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							if(isChecked)
							{
								flag_alarm = isChecked;
								alarm_image.setImageResource(R.drawable.alarm_check);
							}
							else
							{
								flag_alarm = isChecked;
								alarm_image.setImageResource(R.drawable.alarm_x);
							}
						}
					});
					
					save_event.setText("Update Event!");
					
					save_event.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {

							Time t = new Time();
							Schedule sh = new Schedule();
							t.hour = timePicker.getCurrentHour();
							t.minute = timePicker.getCurrentMinute();
							
							sh.setTime(t.toMillis(false));
							sh.setDate(cwdate);
							sh.setTitle(title.getText().toString());
							sh.setDescription(desc.getText().toString());
							sh.setLocation(loc.getText().toString());
							
							
						
							
							if(r.isChecked())
								sh.setType("r");
							
							if(e.isChecked())
								sh.setType("e");

							sh.setAlarm(flag_alarm);							
							updateEventIntoDatabase(sh, Integer.parseInt(sched_id.getText().toString()));
							edit_dialog.cancel();
							loadList(cwdate);
							Toast toast = Toast.makeText(ScheduleActivity.this, "Event Updated! on "+formatDate(cwdate), Toast.LENGTH_SHORT);
							toast.show();
						}
					});
					
				edit_dialog.show();
				
			}
		});
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
    
    public void updateEventIntoDatabase(Schedule s, int id)
    {
    	ContentValues updateIntoDatabase = new ContentValues();
    	
    	updateIntoDatabase.put("title", s.getTitle());
    	updateIntoDatabase.put("desc", s.getDescription());
    	updateIntoDatabase.put("type", s.getType());
    	updateIntoDatabase.put("time", s.getTime());
    	updateIntoDatabase.put("date", s.getDate());
    	updateIntoDatabase.put("alarm", s.isAlarm());
    	updateIntoDatabase.put("location", s.getLocation());
    	
    	dbHelper.updateSpecificId(updateIntoDatabase, TABLE_NAME, id);
    }
    
}
