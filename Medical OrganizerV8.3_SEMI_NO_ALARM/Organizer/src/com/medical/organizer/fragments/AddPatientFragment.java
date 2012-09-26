package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.Patients;
import com.medical.organizer.utilities.Schedule;

public class AddPatientFragment extends Fragment{
	Helper help = new Helper(getActivity());
	private Patients patient = new Patients();
	private Schedule schedule = new Schedule();
	private RadioGroup status_group;
	private long timeInMills;
	private static int status;
	private int mHour;
	private int mMin;
	private static UUID PATIENT_ID;
	private static UUID SCHEDULE_ID;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.input_patient_entry, container, false);
		
		final EditText last_name = (EditText) view.findViewById(R.id.lname);
		final EditText middle_name = (EditText) view.findViewById(R.id.mi);
		final EditText first_name = (EditText) view.findViewById(R.id.fname);
		final EditText address = (EditText) view.findViewById(R.id.addr);
		final EditText age = (EditText) view.findViewById(R.id.age);
		final EditText medical_history = (EditText) view.findViewById(R.id.med_hist);
		status_group = (RadioGroup) view.findViewById(R.id.status);
		
		Button add = (Button) view.findViewById(R.id.add);
		Button cancel = (Button) view.findViewById(R.id.cancel);
		PATIENT_ID = UUID.randomUUID();
	
		
		status_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton checkedButton = (RadioButton) group.findViewById(checkedId);
				boolean checked = false;
				try {
					checked = checkedButton.isChecked();
				} catch (Exception e) {
					Log.d("Needs Improvement", "====Line Here, RadioButton ClearCHECK!====");
				}
				
				if(checked && checkedId == R.id.in)
				{
					status = 1;
					setInPatientRecord();
	
				}
				
				if(checked && checkedId == R.id.out)
				{
					status = 2;
				}
				
			}
		});


		final ArrayList<View> views = new ArrayList<View>();
		views.add(last_name);
		views.add(first_name);
		views.add(address);
		views.add(address);
		views.add(age);
		views.add(medical_history);
		views.add(status_group);

		
		add.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(help.checkInputs(views))
				{
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
					build.setMessage("Are all entries Correct?");	
			    	build.setCancelable(false);
					build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							patient.setPatient_id(String.valueOf(PATIENT_ID));
							patient.setFname(first_name.getText().toString());
							patient.setMi(middle_name.getText().toString());
							patient.setLname(last_name.getText().toString());
							patient.setAge(Integer.parseInt(age.getText().toString()));
							patient.setAddr(address.getText().toString());
							patient.setPat_status(status);
							patient.setMed_history(medical_history.getText().toString());
							help.insert(patient);
							
								if(status == 1)
								{
									//Helper.scheduleRepeatingAlarm(getActivity(), schedule, schedule.getRequestCode(), schedule.getTime());
									
									schedule.setDescription(schedule.getDescription()+": \n"+patient.getMed_history());
									help.insert(schedule);
									
								}
			
							Toast.makeText(getActivity(), "Patient Details Saved!", Toast.LENGTH_SHORT).show();
							((Main) getActivity()).setStatus(status);
							ActionBar ac = getActivity().getActionBar();
				    		ac.show();
							FragmentManager fm = getFragmentManager();
							fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
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
					Toast.makeText(getActivity(), "Please input All Fields!", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		cancel.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				ActionBar ac = getActivity().getActionBar();
	    		ac.show();
				FragmentManager fm = getFragmentManager();
				fm.popBackStack(Main.getBackStackid(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

			}
		});
		
		
		return view;
	}
	
	
	public void setInPatientRecord()
	{

		final Dialog d = new Dialog(getActivity());
		d.setTitle("Set Schedule for Patient Rounds");
		d.setContentView(R.layout.input_rounds_schedule);
			Calendar c = Calendar.getInstance();
			final EditText h_name = (EditText) d.findViewById(R.id.hospital_name_next);
			final EditText h_room = (EditText) d.findViewById(R.id.hospital_room_next);
			TimePicker timePicker = (TimePicker) d.findViewById(R.id.round_schedule);	
			Button add_rounds_setup = (Button) d.findViewById(R.id.save_rounds_setup);
			Button cancel_rounds_setup = (Button) d.findViewById(R.id.cancel_rounds_setup);
			SCHEDULE_ID = UUID.randomUUID();
			c.setTimeInMillis(System.currentTimeMillis());
			timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
			timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
			mHour = timePicker.getCurrentHour();
			mMin = timePicker.getCurrentMinute();
			final View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(3);
			
			timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
				
				public void onTimeChanged(TimePicker tp, int hourOfDay, int minute) {
					mHour = hourOfDay;
					mMin = minute;
				
				}
			});		
			
			final ArrayList<View> views = new ArrayList<View>();
			views.add(h_name);
			views.add(h_room);

			add_rounds_setup.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(help.checkInputs(views))
					{
						AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
						build.setMessage("Are all entries Correct?");	
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
								//Time time = new Time();
								//time.hour = mHour;
								//time.minute = mMin;
								Time time = new Time();
								time.hour = mHour;
								time.minute = mMin;
								time.second = 0;
								timeInMills = time.toMillis(false);
								
								patient.setHosp_name(h_name.getText().toString());
								patient.setHosp_room(h_room.getText().toString());
								schedule.setSchedule_id(String.valueOf(SCHEDULE_ID));
								schedule.setPatient_id(String.valueOf(PATIENT_ID));
								schedule.setTitle("Patient Rounds");
								schedule.setType("rounds");
								schedule.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
								schedule.setDescription("Medical Rounds");
								schedule.setAlarm(true);
								schedule.setDone(false);
								schedule.setTime(timeInMills);
								schedule.setRequestCode(Helper.generateRequestCode(timeInMills*1000));
								schedule.setDate("everyday");
								
								
								Toast.makeText(getActivity(), "Rounds Schedule Set!", Toast.LENGTH_LONG).show();								
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
					else
					{
						Toast.makeText(getActivity(), "Please input All Fields!", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
			cancel_rounds_setup.setOnClickListener(new OnClickListener() {			
				public void onClick(View v) {
					d.cancel();
					//checkedButton.setChecked(false);
					status_group.clearCheck();		
				}
			});
			
		d.show();	
	}
}
