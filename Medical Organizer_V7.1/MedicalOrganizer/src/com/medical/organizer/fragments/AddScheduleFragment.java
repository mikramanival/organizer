package com.medical.organizer.fragments;

import java.util.Calendar;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.others.Schedule;
import com.medical.organizer.others.MedicalDatabaseHelper;;


public class AddScheduleFragment extends Fragment {
	private final static String SCHEDULE_TABLE = "Schedule";
	private MedicalDatabaseHelper dbHelper = new MedicalDatabaseHelper(getActivity());
	private static String date;
	private long timeInMills;
	private static boolean flag_alarm;
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
		date = ((Main) getActivity()).getDate();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		ToggleButton toggle_alarm = (ToggleButton) v.findViewById(R.id.toggle_alarm);
		
		//EditText
		final EditText title = (EditText) v.findViewById(R.id.title);
		final EditText description = (EditText) v.findViewById(R.id.desc);
		final EditText location = (EditText) v.findViewById(R.id.location);
		final RadioButton reminder = (RadioButton) v.findViewById(R.id.reminder);
		final RadioButton event = (RadioButton) v.findViewById(R.id.event);
		
		//Timepicker
		TimePicker timePicker = (TimePicker) v.findViewById(R.id.time_picker);
		final ImageView alarm_icon = (ImageView) v.findViewById(R.id.imageView1);
		//TOGGLE BUTTON
		
		Button save_event = (Button) v.findViewById(R.id.save_event);
		Button cancel_event = (Button) v.findViewById(R.id.cancel_edit_sched);
		
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		toggle_alarm.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked)
					alarm_icon.setImageResource(R.drawable.alarm_check);
				else
					alarm_icon.setImageResource(R.drawable.alarm_x);
				
				flag_alarm = isChecked;
			}
		});
		
		timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
			
			public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				Time time = new Time();
				time.hour = hourOfDay;
				time.minute = minute;
				timeInMills = time.toMillis(false);
			}
		});
		
		save_event.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Schedule s = new Schedule();
				s.setTitle(title.getText().toString());
				s.setDescription(description.getText().toString());
				s.setLocation(location.getText().toString());
				s.setDate(date);
				s.setTime(timeInMills);
				if(reminder.isChecked())
					s.setType("reminder");
				
				if(event.isChecked())
					s.setType("events");
				
				s.setAlarm(flag_alarm);
				setEventIntoDatabase(s);
				((Main) getActivity()).setType(s.getType());
				Toast.makeText(getActivity(), "Event Saved!", Toast.LENGTH_SHORT).show();
				
				ActionBar ac = getActivity().getActionBar();
	    		ac.show();
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		
		cancel_event.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Cancel!", Toast.LENGTH_SHORT).show();
				ActionBar ac = getActivity().getActionBar();
	    		ac.show();
				FragmentManager fm = getFragmentManager();
				fm.popBackStack();
			}
		});
		return v;
		
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
    	
    	dbHelper.insertIntoDatabase(SCHEDULE_TABLE, insertIntoDatabase);
    }
}
