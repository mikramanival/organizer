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
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
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
import android.widget.ImageButton;
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
import com.medical.organizer.utilities.PatientSchedule;
import com.medical.organizer.utilities.Patients;
import com.medical.organizer.utilities.Schedule;


public class ScheduleListFragment extends ListFragment implements TextWatcher {
	private Helper help = new Helper(getActivity());
	private ArrayList<Schedule> master_list;
	private ArrayList<Schedule> filtered_list = new ArrayList<Schedule>();
	private long timeInMills;
	private int mHour;
	private int mMin;
	private static String DATE;
	private Button save;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new Schedule();
		DATE = PatientSchedule.formatDate(Main.CALENDAR.getTimeInMillis(), "MM/dd/yyyy");
		String someDate = PatientSchedule.formatDate(Main.CALENDAR.getTimeInMillis(), "EEEEE, MMMMM, dd yyyy");
		Toast.makeText(getActivity(), someDate, Toast.LENGTH_SHORT).show();
		//Toast.makeText(getActivity(), DATE, Toast.LENGTH_SHORT).show();
		loadScheduleMasterList();
		filterScheduleType(master_list, ((Main) getActivity()).getType(),ScheduleListFragment.DATE);
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
			String dateCompare =PatientSchedule.formatDate(System.currentTimeMillis(), "MM/dd/yyyy");
			if(Main.CALENDAR.getTimeInMillis() >= System.currentTimeMillis() || DATE.equals(dateCompare) )
			{
				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ActionBar ac = getActivity().getActionBar();
	    		ac.hide();
	    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				int stackId = ft.replace(R.id.fragment_container, new AddScheduleFragment()).addToBackStack("AddPatient").commit();
				Main.setBackStackid(stackId);
			}
			else
			{
				Toast.makeText(getActivity(), "Cannot Add Event before Current Date", Toast.LENGTH_SHORT).show();
			}
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
							Log.d("options", "event");
							filterScheduleType(master_list, "event", DATE);
				    		//Toast.makeText(getActivity(), "Out-Patients", Toast.LENGTH_SHORT).show();
						}
						d.cancel();
					}
				});
		    d.show();
			return true;
			
		case R.id.back:
			FragmentManager fm = getFragmentManager();
			fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
		//Log.d("ITEMCHECK", "Schedule Request Code: "+schedule.getRequestCode());
		//Log.d("ITEMCHECK", "Schedule Time: "+schedule.getTime());
		//Log.d("ITEMCHECK", "Schedule Date: "+schedule.getDate());
		//Log.d("ITEMCHECK", "=============================================");
		
		MenuItem edit = menu.add("Edit "+Character.toUpperCase(schedule.getType().charAt(0)) + schedule.getType().substring(1));
		MenuItem delete = menu.add("Delete "+Character.toUpperCase(schedule.getType().charAt(0)) + schedule.getType().substring(1));
		
		
		
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if(!schedule.getType().equals("rounds"))
					editSchedule(schedule);
				else
					editRoundSchedule(schedule);
					//Toast.makeText(getActivity(), "Can't Edit as of the Moment!", Toast.LENGTH_SHORT).show();
				
				return true;
			}
		});

		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
					deleteSchedule(schedule);
				return true;
			}
		});
		
		if(!schedule.getType().equals("rounds"))
		{
			if(schedule.isAlarm())
			{
				MenuItem alarmOff = menu.add("Turn Alarm Off");
				alarmOff.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
							Schedule s = schedule;
							s.setSchedule_id(schedule.getSchedule_id());
							s.setAlarm(false);
							if(!s.isDone())
							{
								s.setDone(true);
								help.cancelAlarm(getActivity(), s.getRequestCode());
							}
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
							
							TimeDialogFragment timeDialog = new TimeDialogFragment();
							Main.timeListener = new OnTimeSetListener() {
								public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
									Main.CALENDAR.set(Calendar.HOUR_OF_DAY, hourOfDay);
									Main.CALENDAR.set(Calendar.MINUTE, minute);
									Main.CALENDAR.set(Calendar.SECOND, 0);
									Main.CALENDAR.set(Calendar.MILLISECOND, 0);
									Schedule s = schedule;
									s.setSchedule_id(schedule.getSchedule_id());
									s.setAlarm(true);
									s.setDone(false);
									s.setTime(Main.CALENDAR.getTimeInMillis());
									
										if(s.getRequestCode() == 0)
											s.setRequestCode(Helper.generateRequestCode(s.getTime()));
										
										help.scheduleAlarm(getActivity(), s.getSchedule_id(), s.getRequestCode(), s.getTime());
										
									help.update(s, Helper.CHANGE_ALARM_STATUS);
									loadScheduleMasterList();
									filterScheduleType(master_list, schedule.getType(), DATE);
								}
							};
							timeDialog.show(getFragmentManager(), "timeDialogPicker");
						return true;
					}
				});
			}
		}
	}
	
	private void viewPatient(final Patients p)
    {
		Dialog info = new Dialog(getActivity());
		info.setTitle("Patient Details");
		info.setContentView(R.layout.patient_list_item_details_fragment);
		
			TextView fname = (TextView) info.findViewById(R.id.first_name_info);
			TextView mname = (TextView) info.findViewById(R.id.middle_name_info);
			TextView lname = (TextView) info.findViewById(R.id.last_name_info);
			TextView age = (TextView) info.findViewById(R.id.age);
			TextView status = (TextView) info.findViewById(R.id.status_info);
			TextView address = (TextView) info.findViewById(R.id.address_info);
			TextView hosp_name = (TextView) info.findViewById(R.id.hosp_name_info);
			TextView hosp_room = (TextView) info.findViewById(R.id.hosp_room_info);
			
			Button see_diag_info = (Button) info.findViewById(R.id.button1);
			see_diag_info.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Dialog details = new Dialog(getActivity());
					details.setTitle("Medical History");
					TextView history = new TextView(getActivity());
					history.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
					history.setText(p.getMed_history());
					details.setContentView(history);
					details.show();
				}
			});
			
			fname.setText(p.getFname());
			mname.setText(p.getMi());
			lname.setText(p.getLname());
			age.setText(Integer.toString(p.getAge()));
			if(p.getPat_status() == 1)
			{
				status.setText("In-Patient");
				hosp_name.setText(p.getHosp_name());
				hosp_room.setText(p.getHosp_room());
			}
			
			if(p.getPat_status() == 2)
			{
				status.setText("Out-Patient");
				hosp_name.setText("None..");
				hosp_room.setText("None..");
				hosp_name.setEnabled(false);
				hosp_room.setEnabled(false);
			}

			address.setText(p.getAddr());
		info.show();
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

		type.setText(Character.toUpperCase(schedule.getType().charAt(0))+schedule.getType().substring(1));
		title.setText(schedule.getTitle());
		date_time.setText(PatientSchedule.formatDate(schedule.getTime(), "hh:mm a"));
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
						help.cancelAlarm(getActivity(), schedule.getRequestCode());
						help.delete(schedule);
						help.update(p, Helper.CHANGE_PATIENT_STATUS);
					}
					else
					{
						if(schedule.isAlarm())
							help.cancelAlarm(getActivity(), schedule.getRequestCode());
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
		save = (Button) in_d.findViewById(R.id.save_rounds_setup);
		Button cancel_rounds_setup = (Button) in_d.findViewById(R.id.cancel_rounds_setup);
		
		save.setEnabled(false);
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
				save.setEnabled(true);
			}
		});	
				
		h_name.addTextChangedListener(this);
		h_room.addTextChangedListener(this);
		
		save.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
				build.setMessage("Are all edits Correct?");	
		    	build.setCancelable(false);
				build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
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
				
						Main.CALENDAR.set(Calendar.HOUR_OF_DAY, mHour);
						Main.CALENDAR.set(Calendar.MINUTE, mMin);
						Main.CALENDAR.set(Calendar.SECOND, 0);
						Main.CALENDAR.set(Calendar.MILLISECOND, 0);
						timeInMills = Main.CALENDAR.getTimeInMillis();
						
						Schedule s = schedule;
						
						s.setHosp_name(h_name.getText().toString());
						s.setHosp_room(h_room.getText().toString());
						
						s.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
						s.setTime(timeInMills);

						help.update(s,Helper.UPDATE_SCHEDULE);
						Patients patient = help.getScheduledPatient(s);
						patient.setTime(s.getTime());
						patient.setRequestCode(s.getRequestCode());
						patient.setSchedule_id(s.getSchedule_id());

						help.cancelAlarm(getActivity(), patient.getRequestCode());
						help.scheduleRepeatingAlarm(getActivity(), patient.getSchedule_id(), patient.getRequestCode(), patient.getTime());
						Toast.makeText(getActivity(), "Rounds Schedule Updated!", Toast.LENGTH_SHORT).show();
						
						in_d.cancel();
						loadScheduleMasterList();
						filterScheduleType(master_list, s.getType(), DATE);
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
		final RadioGroup type_group = (RadioGroup) d.findViewById(R.id.type);
		
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
		if((schedule.getType()).equals("event"))
			event.setChecked(true);
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMin = minute;
				save.setEnabled(true);
			}
		});	
		
		type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				save.setEnabled(true);
			}
		});
		
		title.addTextChangedListener(this);
		description.addTextChangedListener(this);
		location.addTextChangedListener(this);
		
		save = (Button) d.findViewById(R.id.save_event);
		save.setEnabled(false);
		Button cancel_event = (Button) d.findViewById(R.id.cancel_edit_sched);
			
			save.setOnClickListener(new OnClickListener() {		
				public void onClick(View v) {
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
					build.setMessage("Are all edits Correct?");	
			    	build.setCancelable(false);
					build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
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
							Main.CALENDAR.set(Calendar.HOUR_OF_DAY, mHour);
							Main.CALENDAR.set(Calendar.MINUTE, mMin);
							timeInMills = Main.CALENDAR.getTimeInMillis();
							s.setTitle(title.getText().toString());
							s.setLocation(location.getText().toString());
							s.setDescription(description.getText().toString());
							s.setTime(timeInMills);
							
							if(reminder.isChecked())
								s.setType("reminder");
							
							if(event.isChecked())
								s.setType("event");
							
							if(s.isAlarm())
								help.scheduleAlarm(getActivity(), s.getSchedule_id(), s.getRequestCode(), s.getTime());
						
							help.update(s, Helper.NORMAL);
							loadScheduleMasterList();
							filterScheduleType(master_list, s.getType(), DATE);
							Toast.makeText(getActivity(), "Update Saved!", Toast.LENGTH_SHORT).show();
							d.cancel();
							
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
		master_list = convertScheduleList(help.getData(this, DATE));
		if(master_list.size() <= 0)
			Toast.makeText(getActivity(), "Events List Empty", Toast.LENGTH_LONG).show();
		
	}
		
	private void filterScheduleType(ArrayList<Schedule> list, String type, String date)
	{ 	
		String notiff_type = Character.toUpperCase(type.charAt(0))+type.substring(1);
		Toast.makeText(getActivity(), notiff_type, Toast.LENGTH_SHORT).show();
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
				final Schedule schedule = (Schedule) getListAdapter().getItem(arg2);
				if(schedule.getType().equals("rounds"))
				{
					final Dialog d = new Dialog(getActivity());
					d.setTitle("View Choice");
					d.setContentView(R.layout.schedule_choice_fragment);
					
					ImageButton pat = (ImageButton) d.findViewById(R.id.patient);
					ImageButton sched = (ImageButton) d.findViewById(R.id.schedule);
					
					pat.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							Patients patient = help.getScheduledPatient(schedule);
							viewPatient(patient);
						}
					});
					
					sched.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							viewScheduleDetails(schedule);
						}
					});
					
					d.show();
				}
				else
				{
					viewScheduleDetails(schedule);
				}
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

	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		save.setEnabled(true);
	}

	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		save.setEnabled(true);
	}
	
}
