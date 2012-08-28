package com.medical.organizer.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.text.format.Time;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.adapters.PatientAdapter;
import com.medical.organizer.others.MedicalDatabaseHelper;
import com.medical.organizer.others.Patients;
import com.medical.organizer.others.Schedule;

public class PatientListFragment extends ListFragment {
	private final static String PATIENT_TABLE = "Patients";
	private final static String SCHEDULE_TABLE = "Schedule";
	private MedicalDatabaseHelper dbHelper = new MedicalDatabaseHelper(getActivity());
	private ArrayList<Patients> patient_list = new ArrayList<Patients>();
	private Schedule schedule = new Schedule();
	private Patients patient = new Patients();
	private long timeInMills;
	private static int status;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		loadPatientList(((Main) getActivity()).getStatus());
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		View view = info.targetView;
    	final TextView id = (TextView) view.findViewById(R.id.id);
		status = dbHelper.checkIdStatus(PATIENT_TABLE, id.getText().toString());
		
		MenuItem edit = menu.add("Edit");
		MenuItem delete = menu.add("Delete");
		
		if(status == 1)
		{
			MenuItem change_status = menu.add("Change Status");
    		change_status.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					changePatientStatus(id.getText().toString());
					return true;
				}
			});
		}
		
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				//Toast.makeText(getActivity(), "EDIT ID: "+id.getText().toString(), Toast.LENGTH_SHORT).show();
				editPatient(id.getText().toString());
				return true;
			}
		});
		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
				Toast.makeText(getActivity(), "DELETE ID: "+id.getText().toString(), Toast.LENGTH_SHORT).show();
				deletePatient(id.getText().toString());
				return true;
			}
		});

		
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
		
		inflater.inflate(R.menu.patient_list_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	if(item.getItemId() == R.id.filter)
    	{
    		//Toast.makeText(getActivity(), "FILTER!", Toast.LENGTH_SHORT).show();
    		final Dialog d = new Dialog(getActivity());
    		d.setTitle("Filter List");
    		d.setContentView(R.layout.filter_choices);
    			RadioGroup rgroup = (RadioGroup) d.findViewById(R.id.options_menu);
    			
    			rgroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton) group.findViewById(checkedId);
						if(rb.isChecked() && rb.getId() == R.id.all_patient)
						{
				    		Log.d("options", "all-patients here!");
				    		status = 0;
				    		loadPatientList(status);
				    		Toast.makeText(getActivity(), "All-Patients", Toast.LENGTH_LONG).show();
				    		
						}
						
						if(rb.isChecked() && rb.getId() == R.id.in_patient)
						{
							Log.d("options", "in-patients here!");
							status = 1;
							loadPatientList(status);
							Toast.makeText(getActivity(), "In-Patients", Toast.LENGTH_LONG).show();
				    	
						}
						
						if(rb.isChecked() && rb.getId() == R.id.out_patient)
						{
							Log.d("options", "out-patients here!");
							status = 2;
							loadPatientList(status);
							Toast.makeText(getActivity(), "Out-Patients", Toast.LENGTH_LONG).show();
						}
						d.cancel();
					}
				});
		    d.show();
    	}
    	
    	if(item.getItemId() == R.id.add)
    	{
    		//Toast.makeText(getActivity(), "ADD!", Toast.LENGTH_SHORT).show();
    	
    		FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ActionBar ac = getActivity().getActionBar();
    		ac.hide();
    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			ft.replace(R.id.fragment_container, new AddPatientFragment()).addToBackStack("AddPatient").commit();
	
    	}

		return super.onOptionsItemSelected(item);
	}
	
	public void loadPatientList(int patient_status)
	{
		Cursor c;
		patient_list.clear();
				switch (patient_status) 
				{
					case 1:
						 c = dbHelper.retrieveAllDataWhere(PATIENT_TABLE,1);
						 Toast.makeText(getActivity(), "In-Patients", Toast.LENGTH_LONG).show();
						break;
					case 2:
						 c = dbHelper.retrieveAllDataWhere(PATIENT_TABLE,2);
						 Toast.makeText(getActivity(), "Out-Patients", Toast.LENGTH_LONG).show();
						break;
						
					default:
						c = dbHelper.retrieveAllData(PATIENT_TABLE);
						Toast.makeText(getActivity(), "All-Patients", Toast.LENGTH_LONG).show();
						break;
				}
			
		c.moveToFirst();
		   
		while(!c.isAfterLast())
		{
			Patients p = new Patients();
			Log.d("patient id",Integer.toString(c.getInt(c.getColumnIndex("_id"))));
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
			patient_list.add(p);
		   		
		   	c.moveToNext();
		}
		
		dbHelper.close();
		
		Collections.sort(patient_list, new Comparator<Patients>() {
		   public int compare(Patients o1, Patients o2) {
		      return o1.getLname().compareToIgnoreCase(o2.getLname());
		   }
		});
		
		for(Patients item : patient_list)
		{
			Log.d("itemCheck",item.toString());
		}
		
		PatientAdapter patientListAdapter = new PatientAdapter(getActivity(),R.layout.patient_list_item_fragment, patient_list);
		setListAdapter(patientListAdapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View v, int arg2,
					long arg3) {
				TextView tv = (TextView) v.findViewById(R.id.id);
				String id = tv.getText().toString();
				viewPatientDetails(id);
			}
		});
		
		registerForContextMenu(getListView());
	}
	
	private void deletePatient(final String id)
    {
    	AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setMessage("Do you really want to Delete this Entry?");
		build.setCancelable(false);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				try {
					Toast.makeText(getActivity(), "Id to be deleted is:: "+id, Toast.LENGTH_SHORT).show();
					dbHelper.deleteRecordById(PATIENT_TABLE, id);
					switch (status) {
						case 1:
							Toast.makeText(getActivity(), "Status == 1 || Id to be deleted is:: "+id, Toast.LENGTH_SHORT).show();
							dbHelper.deletePatientRoundsById(id);
							loadPatientList(1);
							break;
						case 2:
							loadPatientList(2);
							break;
							
						default:
							loadPatientList(0);
							break;
					}
				
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				Toast toast = Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT);
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
	
    private void editPatient(String id)
    {
		Cursor c = dbHelper.retrieveSpecificId(PATIENT_TABLE,id);
		c.moveToFirst();
		patient.setId(c.getString(c.getColumnIndex("_id")));
		patient.setFname(c.getString(c.getColumnIndex("firstname")));
		patient.setMi(c.getString(c.getColumnIndex("middlename")));
		patient.setLname(c.getString(c.getColumnIndex("lastname")));
		patient.setAddr(c.getString(c.getColumnIndex("address")));
		patient.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
		patient.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));
		patient.setAge(c.getInt(c.getColumnIndex("age")));
		patient.setMed_history(c.getString(c.getColumnIndex("med_history")));	
		
		dbHelper.close();
		
		
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Update Entry");
		d.setContentView(R.layout.input_patient_entry);
			TextView header = (TextView) d.findViewById(R.id.header);
			header.setText("Update Contact Entry");
			final TextView full_name = (TextView) d.findViewById(R.id.fname);
			final TextView middle_name = (TextView) d.findViewById(R.id.mi);
			final TextView last_name = (TextView) d.findViewById(R.id.lname);
			final TextView address = (TextView) d.findViewById(R.id.addr);
			final TextView age = (TextView) d.findViewById(R.id.age);
			final TextView medical_history = (TextView) d.findViewById(R.id.med_hist);
			RadioGroup status_group = (RadioGroup) d.findViewById(R.id.status);
			status_group.setVisibility(RadioGroup.GONE);

			Button save = (Button) d.findViewById(R.id.add);
			Button cancel = (Button) d.findViewById(R.id.cancel);

			save.setText("Update");
			full_name.setText(patient.getFname());
			middle_name.setText(patient.getMi());
			last_name.setText(patient.getLname());
			address.setText(patient.getAddr());
			age.setText(Integer.toString(patient.getAge()));
			medical_history.setText(patient.getMed_history());
			
			
			save.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {

					patient.setFname(full_name.getText().toString());
					patient.setMi(middle_name.getText().toString());
					patient.setLname(last_name.getText().toString());
					patient.setAddr(address.getText().toString());
					patient.setAge(Integer.parseInt(age.getText().toString()));
					patient.setMed_history(medical_history.getText().toString());
					updateInformation(patient);
					((Main) getActivity()).setStatus(status);
					d.cancel();
					Toast toast = Toast.makeText(getActivity(), "Updated!", Toast.LENGTH_SHORT);
					toast.show();
				}
			});
			cancel.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.cancel();
				}
			});
			
			
		d.show();
    }
    
    private void changePatientStatus(final String id)
    {
		Cursor c_sched = dbHelper.retriveRoundsInfo(id);
	 	c_sched.moveToFirst();
		schedule.setTime(c_sched.getLong(c_sched.getColumnIndex("time")));
		dbHelper.close();
		
    	Toast.makeText(getActivity(), "Update Hospital", Toast.LENGTH_SHORT).show();
		final Dialog in_d = new Dialog(getActivity());
		in_d.setTitle("Update Hospital Details");
		in_d.setContentView(R.layout.input_rounds_schedule);
		
		Calendar c = Calendar.getInstance();
		final EditText h_name = (EditText) in_d.findViewById(R.id.hospital_name_next);
		final EditText h_room = (EditText) in_d.findViewById(R.id.hospital_room_next);
		TimePicker timePicker = (TimePicker) in_d.findViewById(R.id.round_schedule);	
		Button add_rounds_setup = (Button) in_d.findViewById(R.id.save_rounds_setup);
		Button cancel_rounds_setup = (Button) in_d.findViewById(R.id.cancel_rounds_setup);
		
		c.setTimeInMillis(schedule.getTime());
		Toast.makeText(getActivity(), "Time of this record is: "+formatTime(c.getTimeInMillis()), Toast.LENGTH_LONG).show();

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
				Toast.makeText(getActivity(), "Time of this record is: "+formatTime(timeInMills), Toast.LENGTH_LONG).show();

				Schedule new_schedule = new Schedule();
				Patients new_patient = new Patients();
				new_patient.setId(id);
				new_schedule.setPatient_id(id);
				new_patient.setHosp_name(h_name.getText().toString());
				new_patient.setHosp_room(h_room.getText().toString());
				new_schedule.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
				new_schedule.setTime(timeInMills);
				
				Toast.makeText(getActivity(), "Rounds Schedule Updated!", Toast.LENGTH_LONG).show();
				dbHelper.changePatientStatus(PATIENT_TABLE, 1, id);
				updateScheduleInfo(new_schedule);
				updatePatientInfo(new_patient);
				((Main) getActivity()).setStatus(1);
				in_d.cancel();
			}
		});
		
		cancel_rounds_setup.setOnClickListener(new OnClickListener() {			
			public void onClick(View v) {
				in_d.cancel();
			}
		});
		
		in_d.show();
    }
    
    private void viewPatientDetails(String id)
    {
    	final Patients p = new Patients();

		Cursor c = dbHelper.retrieveSpecificId(PATIENT_TABLE,id);
		c.moveToFirst();

		p.setFname(c.getString(c.getColumnIndex("firstname")));
		p.setMi(c.getString(c.getColumnIndex("middlename")));
		p.setLname(c.getString(c.getColumnIndex("lastname")));
		p.setAddr(c.getString(c.getColumnIndex("address")));
		p.setHosp_name(c.getString(c.getColumnIndex("hosp_name")));
		p.setHosp_room(c.getString(c.getColumnIndex("hosp_room")));
		p.setAge(c.getInt(c.getColumnIndex("age")));
		p.setMed_history(c.getString(c.getColumnIndex("med_history")));	
		p.setPat_status(c.getInt(c.getColumnIndex("status")));

		dbHelper.close();

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
    
    private void updatePatientInfo(Patients p)
    {
    	ContentValues valuesToUpdate = new ContentValues();
    	valuesToUpdate.put("hosp_name",p.getHosp_name());
    	valuesToUpdate.put("hosp_room", p.getHosp_room());
    	Log.d("dbcheck","Values of updatePatientInfo()"+valuesToUpdate.toString());
    	Log.d("dbcheck","Update to ID: "+p.getId());
    	dbHelper.updateSpecificId(valuesToUpdate, PATIENT_TABLE, p.getId());
    }
    
    private void updateScheduleInfo(Schedule s)
    {
    	ContentValues valuesToUpdate = new ContentValues();
    	valuesToUpdate.put("time",s.getTime());
    	valuesToUpdate.put("location",s.getLocation());
    	Log.d("dbcheck","Values of updateScheduleInfo()"+valuesToUpdate.toString());
    	Log.d("dbcheck","Update to ID: "+s.getPatient_id());
    	dbHelper.updateRoundSchedule(valuesToUpdate, SCHEDULE_TABLE, s.getPatient_id());
    }
    
    private void updateInformation(Patients p){

    	ContentValues valuesToUpdate = new ContentValues();
    	valuesToUpdate.put("firstname", p.getFname());
    	valuesToUpdate.put("middlename", p.getMi());
    	valuesToUpdate.put("lastname", p.getLname());
    	valuesToUpdate.put("address", p.getAddr());
    	valuesToUpdate.put("age", p.getAge());
    	valuesToUpdate.put("med_history", p.getMed_history());
    	valuesToUpdate.put("hosp_name", p.getHosp_name());
    	valuesToUpdate.put("hosp_room", p.getHosp_room());
    	valuesToUpdate.put("status", p.getPat_status());
    	dbHelper.updateSpecificId(valuesToUpdate, PATIENT_TABLE, p.getId());
    }
	
    private String formatTime(long millisec)
    {
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
    	Calendar cal = Calendar.getInstance();
    	cal.setTimeInMillis(millisec);
    	return format.format(cal.getTime());
    }
   
	
}
