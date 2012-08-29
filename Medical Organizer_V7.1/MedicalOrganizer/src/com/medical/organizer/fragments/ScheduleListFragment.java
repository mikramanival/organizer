package com.medical.organizer.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.adapters.ScheduleAdapter;
import com.medical.organizer.others.MedicalDatabaseHelper;
import com.medical.organizer.others.Schedule;

public class ScheduleListFragment extends ListFragment {
	private final static String SCHEDULE_TABLE = "Schedule";
	private MedicalDatabaseHelper dbHelper = new MedicalDatabaseHelper(getActivity());
	private ArrayList<Schedule> schedule_list = new ArrayList<Schedule>();
	private Schedule schedule = new Schedule();
	private String type;
	private static String date;
	private static long dateInMills;
	private long timeInMills;
	private Calendar current = Calendar.getInstance();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		Toast.makeText(getActivity(), "Show Agenda on "+((Main) getActivity()).getDate(), Toast.LENGTH_LONG).show();
		date = ((Main) getActivity()).getDate();
		dateInMills = ((Main) getActivity()).getDateInMills();
		current.setTimeInMillis(System.currentTimeMillis());
		loadScheduleList(date, "rounds");
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.schedule_list_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == R.id.calendar)
		{
			FragmentManager fm = getFragmentManager();
			fm.popBackStack();
		}
		
		if(item.getItemId() == R.id.filter_event_type)
		{
			//Toast.makeText(getActivity(), "Filter Event Type", Toast.LENGTH_LONG).show();
			final Dialog d = new Dialog(getActivity());
    		d.setTitle("Filter List");
    		d.setContentView(R.layout.schedule_filter_choices);
    			RadioGroup rgroup = (RadioGroup) d.findViewById(R.id.options_menu);
    			
    			rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton) group.findViewById(checkedId);
						if(rb.isChecked() && rb.getId() == R.id.events)
						{
							type = "events";	
				    		//Toast.makeText(getActivity(), type+" - "+date, Toast.LENGTH_LONG).show();			
				    		loadScheduleList(date, type);
						}
						
						if(rb.isChecked() && rb.getId() == R.id.reminder)
						{
							type = "reminder";
							loadScheduleList(date,type);
						}
						
						if(rb.isChecked() && rb.getId() == R.id.rounds)
						{
							type = "rounds";	
							loadScheduleList(date,type);
						}
						d.cancel();
					}
				});
		    d.show();
		}
		
		
			if(item.getItemId() == R.id.add_event)
			{
				if(current.getTimeInMillis() <= dateInMills || date.equals(formatDate(current.getTimeInMillis())))
				{
					Toast.makeText(getActivity(), "Add Event", Toast.LENGTH_LONG).show();
		
		    		FragmentManager fm = getFragmentManager();
					FragmentTransaction ft = fm.beginTransaction();
					ActionBar ac = getActivity().getActionBar();
		    		ac.hide();
		    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
					ft.replace(R.id.fragment_container, new AddScheduleFragment()).addToBackStack("AddSchedule").commit();
					ft.addToBackStack("AddSchedule");
				}
				else
				{
					Toast.makeText(getActivity(), "Can't add Event before Current Date", Toast.LENGTH_LONG).show();
				}
			}
		
		
		
		
		return super.onOptionsItemSelected(item);
		
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		View view = info.targetView;
    	final TextView id = (TextView) view.findViewById(R.id.id);
    	
    	Toast.makeText(getActivity(), id.getText().toString(), Toast.LENGTH_SHORT).show();
    	
		MenuItem edit = menu.add("Edit");
		MenuItem delete = menu.add("Delete");
		
		if(dbHelper.checkIdAlarmStatus(Integer.parseInt(id.getText().toString())))
		{
			MenuItem alarm_off = menu.add("Turn Alarm Off");
			
				alarm_off.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					public boolean onMenuItemClick(MenuItem item) {
						dbHelper.setAlarm(false, Integer.parseInt(id.getText().toString()));
						loadScheduleList(date, type);
						return true;
					}
				});
		}
		else
		{
			MenuItem alarm_on = menu.add("Turn Alarm On");	
			
				alarm_on.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					public boolean onMenuItemClick(MenuItem item) {
						dbHelper.setAlarm(true, Integer.parseInt(id.getText().toString()));
						loadScheduleList(date, type);
						return true;
					}
				});
		}
		
				edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					public boolean onMenuItemClick(MenuItem item) {
						editEvent(Integer.parseInt(id.getText().toString()));
						return true;
					}
				});
				
				delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					
					public boolean onMenuItemClick(MenuItem item) {
						 deleteSchedule(Integer.parseInt(id.getText().toString()));
						return true;
					}
				});
			
	}
	
	public void loadScheduleList(String date, String type)
	{
		schedule_list.clear();
		this.type = type;
			if(type.equals("rounds"))
			{
				Toast.makeText(getActivity(), "Rounds", Toast.LENGTH_LONG).show();
				date = "everyday";
			}
			else if(type.equals("events"))
			{
				Toast.makeText(getActivity(), "Events", Toast.LENGTH_LONG).show();
			}
			else if(type.equals("reminder"))
			{
				Toast.makeText(getActivity(), "Reminders", Toast.LENGTH_LONG).show();
			}
		
		if(dbHelper.countRowScehdule(date, type) == 0)
		{
			Toast.makeText(getActivity(), "EMPTY!", Toast.LENGTH_LONG).show();
		}
		else
		{
			Cursor c = dbHelper.retrieveAllEventsWhere(SCHEDULE_TABLE, date, type);
			c.moveToFirst();
			while(!c.isAfterLast())
	    	{
				schedule = new Schedule();
				schedule.setId(c.getInt(c.getColumnIndex("_id")));
				schedule.setTitle(c.getString(c.getColumnIndex("title")));
				schedule.setDescription(c.getString(c.getColumnIndex("desc")));
	    		//schedule.setDate(c.getLong(c.getColumnIndex("date")));
				schedule.setTime(c.getLong(c.getColumnIndex("time")));
				schedule.setType(c.getString(c.getColumnIndex("type")));
	    		//schedule.setLocation(c.getString(c.getColumnIndex("location")));
	    		if(c.getInt(c.getColumnIndex("alarm")) == 1)
	    			schedule.setAlarm(true);
	    		else
	    			schedule.setAlarm(false);
	    		Log.d("dbcheck", schedule.toString());
	    		schedule_list.add(schedule);
	    		c.moveToNext();
	    	}
	    	dbHelper.close();
	    	Log.d("dbcheck", "==============================");
	    	
	    	for(Schedule item: schedule_list)
	    	{
	    		Log.d("dbcheck", item.toString());
	    	}
		}	
	    	ScheduleAdapter scheduleListAdapter = new ScheduleAdapter(getActivity(), R.layout.schedule_list_item_fragment, schedule_list);
	    	setListAdapter(scheduleListAdapter);
	    	
				getListView().setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> arg0, View v,
							int arg2, long arg3) {
						TextView id = (TextView) v.findViewById(R.id.id);
						showScheduleDetails(Integer.parseInt(id.getText().toString()));
					}
				});
				
	    	registerForContextMenu(getListView());
			
			
		
	}

	public void editEvent(final int id)
	{
		Cursor c = dbHelper.retrieveSpecificId(SCHEDULE_TABLE, id);
		Schedule s = new Schedule();
		c.moveToFirst();
		
		while(!c.isAfterLast())
		{
			s.setId(c.getInt(c.getColumnIndex("_id")));
			s.setTitle(c.getString(c.getColumnIndex("title")));
			s.setDescription(c.getString(c.getColumnIndex("desc")));
			s.setLocation(c.getString(c.getColumnIndex("location")));
			s.setTime(c.getLong(c.getColumnIndex("time")));
			s.setDate(c.getString(c.getColumnIndex("date")));
			s.setType(c.getString(c.getColumnIndex("type")));
			
			if((s.getType()).equals("rounds"))
				s.setPatient_id(c.getString(c.getColumnIndex("patient_id")));
			
			//s.setAlarm((c.getInt(c.getColumnIndex("alarm")))!=0);				
			c.moveToNext();
		}
		
		dbHelper.close();
		
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Update Event");
		d.setContentView(R.layout.input_schedule_entry);
		TextView header = (TextView) d.findViewById(R.id.textView4);
		header.setVisibility(TextView.GONE);
		
		ToggleButton toggle_alarm = (ToggleButton) d.findViewById(R.id.toggle_alarm);
		ImageView alarm_icon = (ImageView) d.findViewById(R.id.imageView1);
		
		toggle_alarm.setEnabled(false);
		toggle_alarm.setVisibility(ToggleButton.GONE);
		
		alarm_icon.setEnabled(false);
		alarm_icon.setVisibility(ToggleButton.GONE);
		
		final EditText title = (EditText) d.findViewById(R.id.title);
		final EditText description = (EditText) d.findViewById(R.id.desc);
		final EditText location = (EditText) d.findViewById(R.id.location);
		final RadioButton reminder = (RadioButton) d.findViewById(R.id.reminder);
		final RadioButton event = (RadioButton) d.findViewById(R.id.event);
		final RadioGroup type = (RadioGroup) d.findViewById(R.id.type);

		TimePicker timePicker = (TimePicker) d.findViewById(R.id.time_picker);
		current.setTimeInMillis(s.getTime());
		title.setText(s.getTitle());
		description.setText(s.getDescription());
		location.setText(s.getLocation());
		timePicker.setCurrentHour(current.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(current.get(Calendar.MINUTE));
		
		if((s.getType()).equals("rounds"))
		{
			type.setVisibility(RadioGroup.GONE);
			title.setEnabled(false);
			description.setEnabled(false);
			location.setEnabled(false);
		}
		
		
		if((s.getType()).equals("reminder"))
			reminder.setChecked(true);
		if((s.getType()).equals("events"))
			event.setChecked(true);
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				Time time = new Time();
				time.hour = hourOfDay;
				time.minute = minute;
				timeInMills = time.toMillis(false);
			}
		});
		
		Button save_event = (Button) d.findViewById(R.id.save_event);
		Button cancel_event = (Button) d.findViewById(R.id.cancel_edit_sched);
			
			save_event.setOnClickListener(new OnClickListener() {		
				public void onClick(View v) {
					Schedule s = new Schedule();
					s.setTitle(title.getText().toString());
					s.setLocation(location.getText().toString());
					s.setDescription(description.getText().toString());
					s.setTime(timeInMills);
					
					if(reminder.isChecked())
						s.setType("reminder");
					
					if(event.isChecked())
						s.setType("events");
					updateEventIntoDatabase(s, id);
					Toast.makeText(getActivity(), "Update Saved!", Toast.LENGTH_SHORT).show();
					loadScheduleList(date, s.getType());
					d.cancel();
				}
			});
			
			cancel_event.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.cancel();
				}
			});
		
		d.show();
	}
	
	public void deleteSchedule(final int id)
    {

		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setMessage("Do you really want to Delete this Event/Reminder?");
		build.setCancelable(false);
		
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
			
				dbHelper.deleteRecordById(SCHEDULE_TABLE, id);
				loadScheduleList(date, type);
				
				Toast toast = Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_LONG);
				toast.show();
				
			}
		});
		
		build.setNegativeButton("No", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		
		AlertDialog alert = build.create();
		alert.show();
		
    }
	
	public void showScheduleDetails(final int id)
	{
    	Dialog details = new Dialog(getActivity());
    	details.setTitle("Event");
    	details.setContentView(R.layout.schedule_list_item_details_fragment);
	
		TextView title = (TextView) details.findViewById(R.id.title_info);
		TextView location = (TextView) details.findViewById(R.id.location_info);
		TextView description = (TextView) details.findViewById(R.id.desc_info);
		TextView date_time = (TextView) details.findViewById(R.id.date_time_info);
		TextView type = (TextView) details.findViewById(R.id.type_info);
		ImageView alarm_notiff = (ImageView) details.findViewById(R.id.alarm_clock_info);
		
		Schedule s = new Schedule();
		Cursor c = dbHelper.retrieveSpecificId(SCHEDULE_TABLE, id);
		c.moveToFirst();
		
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
		
		dbHelper.close();
		type.setText(s.getType());
		title.setText(s.getTitle());
		date_time.setText(s.formatDate_Time());
		location.setText(s.getLocation());
		description.setText(s.getDescription());
		
			if(s.isAlarm())
				alarm_notiff.setVisibility(ImageView.VISIBLE);
			else
				alarm_notiff.setVisibility(ImageView.INVISIBLE);

		details.show();
		
	}
	
	public void updateEventIntoDatabase(Schedule s, int id)
    {
    	ContentValues updateIntoDatabase = new ContentValues();
    	
    	updateIntoDatabase.put("title", s.getTitle());
    	updateIntoDatabase.put("desc", s.getDescription());
    	updateIntoDatabase.put("type", s.getType());
    	updateIntoDatabase.put("time", s.getTime());
    	//updateIntoDatabase.put("date", s.getDate());
    	//updateIntoDatabase.put("alarm", s.isAlarm());
    	updateIntoDatabase.put("location", s.getLocation());
    	
    	dbHelper.updateSpecificId(updateIntoDatabase, SCHEDULE_TABLE, id);
    }
	
    public String formatDate(long millisec)
    {
    	SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millisec);
    	return format.format(cal.getTime());
    }
}
