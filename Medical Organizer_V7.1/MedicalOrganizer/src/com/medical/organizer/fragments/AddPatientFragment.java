package com.medical.organizer.fragments;

import java.util.Calendar;
import java.util.UUID;
import android.app.ActionBar;
import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.others.MedicalDatabaseHelper;
import com.medical.organizer.others.Patients;
import com.medical.organizer.others.Schedule;;

public class AddPatientFragment extends Fragment {
	private final static String PATIENT_TABLE = "Patients";
	private final static String SCHEDULE_TABLE = "Schedule";
	MedicalDatabaseHelper dbHelper = new MedicalDatabaseHelper(getActivity());
	private long timeInMills;
	private Patients patient = new Patients();
	private Schedule schedule = new Schedule();
	private static UUID PATIENT_ID;
	private static int status;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
	}
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.input_patient_entry, container, false);
		TextView header = (TextView) view.findViewById(R.id.header);
		header.setText("New Patient Entry");
		
		final EditText last_name = (EditText) view.findViewById(R.id.lname);
		final EditText middle_name = (EditText) view.findViewById(R.id.mi);
		final EditText first_name = (EditText) view.findViewById(R.id.fname);
		final EditText address = (EditText) view.findViewById(R.id.addr);
		final EditText age = (EditText) view.findViewById(R.id.age);
		final EditText medical_history = (EditText) view.findViewById(R.id.med_hist);
		RadioGroup status_group = (RadioGroup) view.findViewById(R.id.status);
		Button add = (Button) view.findViewById(R.id.add);
		Button cancel = (Button) view.findViewById(R.id.cancel);

		PATIENT_ID = UUID.randomUUID();
		status_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton checkedButton = (RadioButton) group.findViewById(checkedId);
				boolean checked = checkedButton.isChecked();
				if(checked && checkedButton.getId() == R.id.in)
				{
					status = 1;
					final Dialog d = new Dialog(getActivity());
					d.setTitle("Set Schedule for Patient Rounds");
					d.setContentView(R.layout.input_rounds_schedule);
						Calendar c = Calendar.getInstance();
						final EditText h_name = (EditText) d.findViewById(R.id.hospital_name_next);
						final EditText h_room = (EditText) d.findViewById(R.id.hospital_room_next);
						TimePicker timePicker = (TimePicker) d.findViewById(R.id.round_schedule);	
						Button add_rounds_setup = (Button) d.findViewById(R.id.save_rounds_setup);
						Button cancel_rounds_setup = (Button) d.findViewById(R.id.cancel_rounds_setup);
						c.setTimeInMillis(System.currentTimeMillis());
						timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
						timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
						
						timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
							
							public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
								// TODO Auto-generated method stub
								Time time = new Time();
								time.hour = hourOfDay;
								time.minute = minute;
								timeInMills = time.toMillis(false);
							}
						});	
						
						add_rounds_setup.setOnClickListener(new OnClickListener() {
							public void onClick(View v) {
								
								patient.setHosp_name(h_name.getText().toString());
								patient.setHosp_room(h_room.getText().toString());
								schedule.setPatient_id(String.valueOf(PATIENT_ID));
								schedule.setTitle("Patient Rounds");
								schedule.setType("rounds");
								schedule.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
								schedule.setDescription("Medical Rounds");
								schedule.setAlarm(true);
								schedule.setTime(timeInMills);
								schedule.setDate("everyday");
								Toast.makeText(getActivity(), "Rounds Schedule Set!", Toast.LENGTH_LONG).show();
								newRoundsSchedule(schedule);
								d.cancel();
							}
						});
						
						cancel_rounds_setup.setOnClickListener(new OnClickListener() {			
							public void onClick(View v) {
								d.cancel();
							}
						});
						
					d.show();
				}
				
				if(checked && checkedButton.getId() == R.id.out)
				{
					status = 2;
				}
				
			}
		});
		
			
			add.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					patient.setId(String.valueOf(PATIENT_ID));
					patient.setFname(first_name.getText().toString());
					patient.setMi(middle_name.getText().toString());
					patient.setLname(last_name.getText().toString());
					patient.setAge(Integer.parseInt(age.getText().toString()));
					patient.setAddr(address.getText().toString());
					patient.setPat_status(status);
					patient.setMed_history(medical_history.getText().toString());
					
					newPatient(patient);
						Toast.makeText(getActivity(), "Patient Details Saved!", Toast.LENGTH_LONG).show();
						((Main) getActivity()).setStatus(status); 
						ActionBar ac = getActivity().getActionBar();
			    		ac.show();
						FragmentManager fm = getFragmentManager();
						fm.popBackStack();
					
				}
			});
			
			cancel.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					Toast.makeText(getActivity(), "Cancel!", Toast.LENGTH_SHORT).show();
					ActionBar ac = getActivity().getActionBar();
		    		ac.show();
					FragmentManager fm = getFragmentManager();
					fm.popBackStack();
					//ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
					//ft.replace(R.id.fragment_container, new PatientListFragment());
					//ft.commit();
				}
			});
		return view;
	}
	
	 
	    public void newPatient(Patients p)
	    {
	    	ContentValues valuesToinsert = new ContentValues();
	    	valuesToinsert.put("_id", p.getId());
	    	valuesToinsert.put("firstname", p.getFname());
	    	valuesToinsert.put("middlename", p.getMi());
	    	valuesToinsert.put("lastname", p.getLname());
	    	valuesToinsert.put("address", p.getAddr());
	    	valuesToinsert.put("age", p.getAge());
	    	valuesToinsert.put("med_history", p.getMed_history());
	    	valuesToinsert.put("hosp_name", p.getHosp_name());
	    	valuesToinsert.put("hosp_room", p.getHosp_room());
	    	valuesToinsert.put("status", p.getPat_status());
	    	dbHelper.insertIntoDatabase(PATIENT_TABLE, valuesToinsert);
	    }
	    
	    private void newRoundsSchedule(Schedule s)
	    {
	    	ContentValues valuesToinsert = new ContentValues();
	    	valuesToinsert.put("patient_id", s.getPatient_id());
	    	valuesToinsert.put("title", s.getTitle());
	    	valuesToinsert.put("time", s.getTime());
	    	valuesToinsert.put("location", s.getLocation());
	    	valuesToinsert.put("date", s.getDate());
	    	valuesToinsert.put("desc", s.getDescription());
	    	valuesToinsert.put("type", s.getType());
	    	valuesToinsert.put("alarm", s.isAlarm());
	    	dbHelper.insertIntoDatabase(SCHEDULE_TABLE, valuesToinsert);
	    }
 	
}
