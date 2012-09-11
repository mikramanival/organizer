package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
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
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.adapters.ScheduleAdapter;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.Patients;
import com.medical.organizer.utilities.Schedule;


public class ScheduleListFragment extends ListFragment {
	private Helper help = new Helper(getActivity());
	private ArrayList<Schedule> master_list;
	private ArrayList<Schedule> filtered_list = new ArrayList<Schedule>();
	private long timeInMills;
	private int mHour;
	private int mMin;
	private boolean isAlarmOn;
	private static String DATE;
	
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
		DATE = new Schedule().formatDate(Main.getGlobalDateInMills(), "MM/dd/yyyy");
		Toast.makeText(getActivity(), Main.getGlobalDate(), Toast.LENGTH_SHORT).show();
		loadScheduleMasterList();
		filterScheduleType(master_list, "rounds",ScheduleListFragment.DATE);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.schedule_list_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.add:
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ActionBar ac = getActivity().getActionBar();
    		ac.hide();
    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			int stackId = ft.replace(R.id.fragment_container, new AddScheduleFragment()).addToBackStack("AddPatient").commit();
			Main.setBackStackid(stackId);
			return true;
		
		case R.id.filter:
			final Dialog d = new Dialog(getActivity());
    		d.setTitle("Filter List");
    		d.setContentView(R.layout.filter_choices);
    			RadioGroup rgroup_patient = (RadioGroup) d.findViewById(R.id.options_menu_patient);
    			RadioGroup rgroup_schedule = (RadioGroup) d.findViewById(R.id.options_menu_schedule);
    			rgroup_patient.setVisibility(RadioGroup.GONE);
    			
    			rgroup_schedule.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton) group.findViewById(checkedId);
						if(rb.isChecked() && rb.getId() == R.id.rounds)
						{
				    		Log.d("options", "rounds");
				    		filterScheduleType(master_list, "rounds", DATE);
				    		//Toast.makeText(getActivity(), "All-Patients", Toast.LENGTH_SHORT).show();
				    		
						}
						
						if(rb.isChecked() && rb.getId() == R.id.reminder)
						{
							Log.d("options", "reminder");
							filterScheduleType(master_list, "reminder", DATE);
				    		//Toast.makeText(getActivity(), "In-Patients", Toast.LENGTH_SHORT).show();
				    	
						}
						
						if(rb.isChecked() && rb.getId() == R.id.events)
						{
							Log.d("options", "events");
							filterScheduleType(master_list, "events", DATE);
				    		//Toast.makeText(getActivity(), "Out-Patients", Toast.LENGTH_SHORT).show();
						}
						d.cancel();
					}
				});
		    d.show();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Schedule schedule = (Schedule) getListAdapter().getItem(info.position);
		
		if(schedule.getType().equals("rounds"))
			help.getPatientSchedule(schedule);
		//Log.d("ITEMCHECK", "===================if rounds==================");
		//Log.d("ITEMCHECK", "Schedule Patient ID: "+schedule.getPatient_id());
		//Log.d("ITEMCHECK", "Schedule Hospital Name: "+schedule.getHosp_name());
		//Log.d("ITEMCHECK", "Schedule Hospital Room: "+schedule.getHosp_room());
		//Log.d("ITEMCHECK", "=============================================");
		//Log.d("ITEMCHECK", "Schedule ID: "+schedule.getSchedule_id());
		//Log.d("ITEMCHECK", "Schedule Title: "+schedule.getTitle());
		//Log.d("ITEMCHECK", "Schedule Type: "+schedule.getType());
		//Log.d("ITEMCHECK", "Schedule Description: "+schedule.getDescription());
		//Log.d("ITEMCHECK", "Schedule Location: "+schedule.getLocation());
		//Log.d("ITEMCHECK", "Schedule Alarm: "+schedule.isAlarm());
		//Log.d("ITEMCHECK", "Schedule Time: "+schedule.getTime());
		//Log.d("ITEMCHECK", "Schedule Date: "+schedule.getDate());
		//Log.d("ITEMCHECK", "=============================================");
		MenuItem edit = menu.add("Edit Event");
		MenuItem delete = menu.add("Delete Event");
		
		
		
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if(!schedule.getType().equals("rounds"))
					editSchedule(schedule);
				else
					editRoundSchedule(schedule);
					//Toast.makeText(getActivity(), "Can't Edit as of the Moment!", Toast.LENGTH_SHORT).show();
				
				return false;
			}
		});

		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
					deleteSchedule(schedule);
				return true;
			}
		});
		
		if(schedule.isAlarm())
		{
			MenuItem alarmOff = menu.add("Turn Alarm Off");
			alarmOff.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
						Schedule s = schedule;
						s.setSchedule_id(schedule.getSchedule_id());
						s.setAlarm(false);
						help.update(s, Helper.CHANGE_ALARM_STATUS);
						loadScheduleMasterList();
						filterScheduleType(master_list, schedule.getType(), DATE);
					return true;
				}
			});
		}
		else
		{
			MenuItem alarmOn = menu.add("Turn Alarm On");
			alarmOn.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
						Schedule s = schedule;
						s.setSchedule_id(schedule.getSchedule_id());
						s.setAlarm(true);
						help.update(s, Helper.CHANGE_ALARM_STATUS);
						loadScheduleMasterList();
						filterScheduleType(master_list, schedule.getType(), DATE);
					return true;
				}
			});
		}
	}
	
	private void viewScheduleDetails(final Schedule schedule)
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

		type.setText(schedule.getType());
		title.setText(schedule.getTitle());
		date_time.setText(schedule.formatDate(schedule.getTime(), "hh:mm a"));
		location.setText(schedule.getLocation());
		description.setText(schedule.getDescription());
		
			if(schedule.isAlarm())
				alarm_notiff.setVisibility(ImageView.VISIBLE);
			else
				alarm_notiff.setVisibility(ImageView.INVISIBLE);

		details.show();
	}
	
	private void deleteSchedule(final Schedule schedule)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		
		if(schedule.getType().equals("rounds"))
			build.setMessage("Do you really want to Delete this Event Event Entry? Doing so will change the Patient's Status Associated with this Schedule");
		else
			build.setMessage("Do you really want to Delete this Event Entry?");

    	build.setCancelable(false);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				String type = schedule.getType();
					if(schedule.getType().equals("rounds"))
					{
						Patients p = new Patients();
						p.setPatient_id(schedule.getPatient_id());
						p.setPat_status(2);
						help.delete(schedule);
						help.update(p, Helper.CHANGE_PATIENT_STATUS);
					}
					else
					{
						help.delete(schedule);
					}
				Toast.makeText(getActivity(), "Entry Deleted!", Toast.LENGTH_SHORT).show();
				loadScheduleMasterList();
				filterScheduleType(master_list, type, DATE);
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
	
	private void editRoundSchedule(final Schedule schedule)
	{
		final Dialog in_d = new Dialog(getActivity());
		in_d.setTitle("Update Hospital Details");
		in_d.setContentView(R.layout.input_rounds_schedule);
		
		Calendar c = Calendar.getInstance();
		final EditText h_name = (EditText) in_d.findViewById(R.id.hospital_name_next);
		final EditText h_room = (EditText) in_d.findViewById(R.id.hospital_room_next);
		TimePicker timePicker = (TimePicker) in_d.findViewById(R.id.round_schedule);	
		final View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(3);
		Button add_rounds_setup = (Button) in_d.findViewById(R.id.save_rounds_setup);
		Button cancel_rounds_setup = (Button) in_d.findViewById(R.id.cancel_rounds_setup);
		
		
		c.setTimeInMillis(schedule.getTime());
		h_name.setText(schedule.getHosp_name());
		h_room.setText(schedule.getHosp_room());
		timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(c.get(Calendar.MINUTE));

		mHour = timePicker.getCurrentHour();
		mMin = timePicker.getCurrentMinute();
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMin = minute;
			}
		});	
				
		add_rounds_setup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//Temporary Workaround bug on timepicker
				if(amPmView instanceof NumberPicker)
				{
					if(((NumberPicker) amPmView).getValue() == 1)
					{
						if(mHour >= 12 )	
							mHour = mHour - 12;
						mHour = mHour + 12;
					}
					
					if(((NumberPicker) amPmView).getValue() == 0)
					{
						if(mHour >= 12)
							mHour = mHour - 12;
					}
				}

				//Toast.makeText(getActivity(), "Time of this record is: "+formatTime(timeInMills), Toast.LENGTH_LONG).show();
				Time time = new Time();
				time.hour = mHour;
				time.minute = mMin;
				timeInMills = time.toMillis(false);
				
				Schedule s = schedule;
				
				s.setHosp_name(h_name.getText().toString());
				s.setHosp_room(h_room.getText().toString());
				
				s.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
				s.setTime(timeInMills);

				Toast.makeText(getActivity(), "Rounds Schedule Updated!", Toast.LENGTH_SHORT).show();
				help.update(s,Helper.UPDATE_SCHEDULE);
				in_d.cancel();
				loadScheduleMasterList();
				filterScheduleType(master_list, s.getType(), DATE);
			}
		});
		
		cancel_rounds_setup.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				in_d.cancel();
			}
		});
		
		in_d.show();
	}
	
	private void editSchedule(final Schedule schedule)
	{
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Update Event Entry");
		d.setContentView(R.layout.input_schedule_entry);
		TextView header = (TextView) d.findViewById(R.id.textView4);
		header.setVisibility(TextView.GONE);
		Calendar current = Calendar.getInstance();
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

		TimePicker timePicker = (TimePicker) d.findViewById(R.id.time_picker);
		final View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(3);
		current.setTimeInMillis(schedule.getTime());
		title.setText(schedule.getTitle());
		description.setText(schedule.getDescription());
		location.setText(schedule.getLocation());
		timePicker.setCurrentHour(current.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(current.get(Calendar.MINUTE));
		
		mHour = timePicker.getCurrentHour();
		mMin = timePicker.getCurrentMinute();
		
		if((schedule.getType()).equals("reminder"))
			reminder.setChecked(true);
		if((schedule.getType()).equals("events"))
			event.setChecked(true);
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMin = minute;
			}
		});	
		
		
		Button save_event = (Button) d.findViewById(R.id.save_event);
		Button cancel_event = (Button) d.findViewById(R.id.cancel_edit_sched);
			
			save_event.setOnClickListener(new OnClickListener() {		
				public void onClick(View v) {
					if(amPmView instanceof NumberPicker)
					{
						if(((NumberPicker) amPmView).getValue() == 1)
						{
							if(mHour >= 12 )	
								mHour = mHour - 12;
							mHour = mHour + 12;
						}
						
						if(((NumberPicker) amPmView).getValue() == 0)
						{
							if(mHour >= 12)
								mHour = mHour - 12;
						}
					}
					Schedule s = schedule;
					Time time = new Time();
					time.hour = mHour;
					time.minute = mMin;
					timeInMills = time.toMillis(false);
					s.setTitle(title.getText().toString());
					s.setLocation(location.getText().toString());
					s.setDescription(description.getText().toString());
					s.setTime(timeInMills);
					
					if(reminder.isChecked())
						s.setType("reminder");
					
					if(event.isChecked())
						s.setType("events");
					
					Toast.makeText(getActivity(), "Update Saved!", Toast.LENGTH_SHORT).show();
					d.cancel();
					help.update(s, Helper.NORMAL);
					loadScheduleMasterList();
					filterScheduleType(master_list, s.getType(), DATE);
					
				}
			});
			
			cancel_event.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.cancel();
				}
			});
			
		d.show();
	}
	
	private void loadScheduleMasterList()
	{
		master_list = new ArrayList<Schedule>();
		master_list = convertScheduleList(help.getData(this, new Schedule().formatDate(Main.getGlobalDateInMills(), "MM/dd/yyyy")));
		
	}
		
	private void filterScheduleType(ArrayList<Schedule> list, String type, String date)
	{
		filtered_list.clear();
		Iterator<Schedule> i = list.iterator();
		
		while(i.hasNext())
		{
			Schedule s = i.next();
			if(s.getType().equals(type) && (s.getDate().equals(date) || s.getDate().equals("everyday")) )
				filtered_list.add(s);
		}
		
		ScheduleAdapter scheduleAdapter = new ScheduleAdapter(getActivity(), R.layout.schedule_list_item_fragment, filtered_list);
		setListAdapter(scheduleAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Schedule schedule = (Schedule) getListAdapter().getItem(arg2);
				viewScheduleDetails(schedule);
			}
		});
		registerForContextMenu(getListView());
	}
	
	private ArrayList<Schedule> convertScheduleList(ArrayList<Object> o)
	{
		ArrayList<Schedule> new_list = new ArrayList<Schedule>();
		Iterator<Object> i = o.iterator();
		Object obj_item;
		Schedule s;
			while(i.hasNext())
			{
				s = new Schedule();
				obj_item = i.next();
				if(obj_item instanceof Schedule)
					s = (Schedule) obj_item;
				
				new_list.add(s);
			}
		
		return new_list;
	}
	
}
