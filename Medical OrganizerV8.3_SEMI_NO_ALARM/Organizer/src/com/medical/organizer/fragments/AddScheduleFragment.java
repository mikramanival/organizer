package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.PatientSchedule;
import com.medical.organizer.utilities.Schedule;

public class AddScheduleFragment extends Fragment{
	Helper help = new Helper(getActivity());
	private Schedule schedule = new Schedule();
	private long timeInMills;
	private int mHour;
	private int mMin;
	private boolean isAlarmOn;
	private String date;
	private static UUID SCHEDULE_ID;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.input_schedule_entry, container, false);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		ToggleButton toggle_alarm = (ToggleButton) v.findViewById(R.id.toggle_alarm);
		
		//EditText
		final EditText title = (EditText) v.findViewById(R.id.title);
		final EditText description = (EditText) v.findViewById(R.id.desc);
		final EditText location = (EditText) v.findViewById(R.id.location);
		final RadioButton reminder = (RadioButton) v.findViewById(R.id.reminder);
		final RadioButton event = (RadioButton) v.findViewById(R.id.event);
		final RadioGroup type_group = (RadioGroup) v.findViewById(R.id.type);
		
		//Timepicker
		TimePicker timePicker = (TimePicker) v.findViewById(R.id.time_picker);
		final ImageView alarm_icon = (ImageView) v.findViewById(R.id.imageView1);
		final View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(3);

		Button save_event = (Button) v.findViewById(R.id.save_event);
		Button cancel_event = (Button) v.findViewById(R.id.cancel_edit_sched);
		SCHEDULE_ID = UUID.randomUUID(); // START UPGRADE HERE
		
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		mHour = timePicker.getCurrentHour();
		mMin = timePicker.getCurrentMinute();
	
		toggle_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					alarm_icon.setImageResource(R.drawable.alarm_check);
				else
					alarm_icon.setImageResource(R.drawable.alarm_x);
				
				isAlarmOn = isChecked;
			}
		});
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
				mHour = hourOfDay;
				mMin = minute;
			
			}
		});	
		
		final ArrayList<View> views = new ArrayList<View>();
		views.add(title);
		views.add(description);
		views.add(location);
		views.add(type_group);

		
		save_event.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				if(help.checkInputs(views))
				{
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
					build.setMessage("Are all entries Correct?");
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
							Schedule s = new Schedule();
							Main.CALENDAR.set(Calendar.HOUR_OF_DAY, mHour);
							Main.CALENDAR.set(Calendar.MINUTE, mMin);
							Main.CALENDAR.set(Calendar.SECOND, 0);
							Main.CALENDAR.set(Calendar.MILLISECOND, 0);
							timeInMills = Main.CALENDAR.getTimeInMillis();
							s.setSchedule_id(String.valueOf(SCHEDULE_ID));
							s.setTitle(title.getText().toString());
							s.setDescription(description.getText().toString());
							s.setLocation(location.getText().toString());
							s.setTime(timeInMills);
							s.setDate(PatientSchedule.formatDate(s.getTime(), "MM/dd/yyyy"));
							if(reminder.isChecked())
								s.setType("reminder");
							if(event.isChecked())
								s.setType("event");
								
							s.setAlarm(isAlarmOn);
								
								if(isAlarmOn)
								{
									s.setRequestCode(Helper.generateRequestCode(timeInMills));
									s.setDone(false);
									//Helper.scheduleAlarm(getActivity(), s, s.getRequestCode(), s.getTime());
								}
							
							help.insert(s);
							Toast.makeText(getActivity(), "Saved on "+PatientSchedule.formatDate(s.getTime(), "EEEEE MMMM dd, yyyy "), Toast.LENGTH_LONG).show();
							((Main) getActivity()).setType(s.getType());
				
					    	FragmentManager fm = getFragmentManager();
							FragmentTransaction ft = fm.beginTransaction();
							ActionBar ac = getActivity().getActionBar();
					    	ac.show();
				    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
							ft.replace(R.id.fragment_container, new ScheduleListFragment()).commit();
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
				else
				{
					Toast.makeText(getActivity(), "Please input all Fields!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		cancel_event.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Cancel!", Toast.LENGTH_SHORT).show();
				ActionBar ac = getActivity().getActionBar();
	    		ac.show();
				FragmentManager fm = getFragmentManager();
				fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
			}
		});
				
	return v;
	}

}
