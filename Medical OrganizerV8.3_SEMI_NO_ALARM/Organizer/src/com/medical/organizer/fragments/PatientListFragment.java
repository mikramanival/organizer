package com.medical.organizer.fragments;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.UUID;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.medical.organizer.Main;
import com.medical.organizer.R;
import com.medical.organizer.adapters.PatientAdapter;
import com.medical.organizer.utilities.Helper;
import com.medical.organizer.utilities.Patients;
import com.medical.organizer.utilities.Schedule;

public class PatientListFragment extends ListFragment implements TextWatcher{
	private ArrayList<Patients> master_list;
	//private Schedule schedule;
	private Helper help = new Helper(getActivity());
	private long timeInMills;
	private int mHour;
	private int mMin;
	private Button save;
	private static UUID SCHEDULE_ID;
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
		loadPatientMasterList();
		loadPatientList(((Main) getActivity()).getStatus());
	}
	
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.patient_list_fragment, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.filter:
			final Dialog d = new Dialog(getActivity());
    		d.setTitle("Filter List");
    		d.setContentView(R.layout.filter_choices);
    			RadioGroup rgroup_patient = (RadioGroup) d.findViewById(R.id.options_menu_patient);
    			RadioGroup rgroup_schedule = (RadioGroup) d.findViewById(R.id.options_menu_schedule);
    			rgroup_schedule.setVisibility(RadioGroup.GONE);
    			
    			rgroup_patient.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton) group.findViewById(checkedId);
						if(rb.isChecked() && rb.getId() == R.id.all_patient)
						{
				    		Log.d("options", "all-patients here!");
				    		loadPatientList(0);
				    		Toast.makeText(getActivity(), "All-Patients", Toast.LENGTH_SHORT).show();
				    		
						}
						
						if(rb.isChecked() && rb.getId() == R.id.in_patient)
						{
							Log.d("options", "in-patients here!");
							loadPatientList(1);
							Toast.makeText(getActivity(), "In-Patients", Toast.LENGTH_SHORT).show();
				    	
						}
						
						if(rb.isChecked() && rb.getId() == R.id.out_patient)
						{
							Log.d("options", "out-patients here!");
							loadPatientList(2);
							Toast.makeText(getActivity(), "Out-Patients", Toast.LENGTH_SHORT).show();
						}
						d.cancel();
					}
				});
		    d.show();
			return true;
			
		case R.id.add:
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			ActionBar ac = getActivity().getActionBar();
    		ac.hide();
    		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			int stackId = ft.replace(R.id.fragment_container, new AddPatientFragment()).addToBackStack("AddPatient").commit();
			Main.setBackStackid(stackId);
			return true;
		//FOR DEVs ONLY	
		//case R.id.clear:
			//help.clearAll();
			//return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		final Patients patients = (Patients) getListAdapter().getItem(info.position);
		//Toast.makeText(getActivity(), "Full Name of Clicked: "+patients.getId(), Toast.LENGTH_LONG).show();
    	//final Patients patients = (Patients) help.getRecord(PATIENTS, id.getText().toString());
		MenuItem edit = menu.add("Edit");
		MenuItem delete = menu.add("Delete");
		

    	if(patients != null && patients.getPat_status()==1)
   		{
    		Log.d("itemCheck", "Patient ID: "+patients.getPatient_id());
    		Log.d("itemCheck", "Patient Name: : "+patients.toString());
    		help.getPatientSchedule(patients);
    		Log.d("itemCheck", "Schedule ID: "+patients.getSchedule_id());
    		Log.d("itemCheck", "Location : "+patients.getLocation());
    		Log.d("itemCheck", "Hospital Name: "+patients.getHosp_name());
    		Log.d("itemCheck", "Hospital Room: "+patients.getHosp_room());
    		Log.d("itemCheck", "Time: "+patients.time());
    		Log.d("itemCheck", "Request Code: "+patients.getRequestCode());
    		Log.d("itemCheck", "Under this Schedule is: Patient Name: "+patients.toString());
    		Log.d("itemCheck", "Under this Schedule is: Medical History: "+patients.getMed_history());
    		
    		MenuItem update = menu.add("Update Rounds Schedule");
    		update.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				
				public boolean onMenuItemClick(MenuItem item) {
						updateRounds(patients);
					return true;
				}
			});
   		} 
		
    	MenuItem change_status = menu.add("Change Status");
    	
    	change_status.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
					changeStatus(patients);
				return true;
			}
		});
		
		edit.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			public boolean onMenuItemClick(MenuItem item) {
					editPatients(patients);
				return true;
			}
		});
		
		delete.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {	
					deletePatients(patients);
				return true;
			}
	});
		
		
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
	
	private void changeStatus(final Patients p)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
		build.setMessage("Are you sure you want to Change Patient's Status?");
		build.setCancelable(false);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				
				if(p.getPat_status() == 1)
				{
						Patients patients = p;
						Schedule s = new Schedule();
						
						s.setSchedule_id(p.getSchedule_id());
						patients.setPat_status(2);
						//Helper.cancelRepeatingAlarm(getActivity(), p.getRequestCode());
						help.update(patients,Helper.CHANGE_PATIENT_STATUS);
						help.delete(s);
						Toast.makeText(getActivity(), "Patient's Status Changed!", Toast.LENGTH_SHORT).show();
						loadPatientMasterList();
						loadPatientList(2);
				}	
				else
				{
						final Dialog in_d = new Dialog(getActivity());
						in_d.setTitle("Set Rounds Schedule");
						in_d.setContentView(R.layout.input_rounds_schedule);
						
						Calendar c = Calendar.getInstance();
						final EditText h_name = (EditText) in_d.findViewById(R.id.hospital_name_next);
						final EditText h_room = (EditText) in_d.findViewById(R.id.hospital_room_next);
						TimePicker timePicker = (TimePicker) in_d.findViewById(R.id.round_schedule);	
						final View amPmView  = ((ViewGroup)timePicker.getChildAt(0)).getChildAt(3);
						Button add_rounds_setup = (Button) in_d.findViewById(R.id.save_rounds_setup);
						Button cancel_rounds_setup = (Button) in_d.findViewById(R.id.cancel_rounds_setup);
						SCHEDULE_ID = UUID.randomUUID();
						c.setTimeInMillis(System.currentTimeMillis());	
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
											Time time = new Time();
											time.hour = mHour;
											time.minute = mMin;
											time.second = 0;
											timeInMills = time.toMillis(false);
											
											Schedule schedule = new Schedule();
											Patients patient = p;
											
											patient.setHosp_name(h_name.getText().toString());
											patient.setHosp_room(h_room.getText().toString());
											patient.setPat_status(1);
											
											schedule.setSchedule_id(String.valueOf(SCHEDULE_ID));
											schedule.setPatient_id(patient.getPatient_id());
											schedule.setTitle("Patient Rounds");
											schedule.setType("rounds");
											schedule.setDescription("Medical Rounds: \n"+patient.getMed_history());
											schedule.setAlarm(true);
											schedule.setDate("everyday");
											schedule.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
											schedule.setDone(false);
											schedule.setTime(timeInMills);
											schedule.setRequestCode(Helper.generateRequestCode(timeInMills*1000));						
											help.update(patient, Helper.CHANGE_PATIENT_STATUS);
											help.insert(schedule);
											//Helper.scheduleRepeatingAlarm(getActivity(), schedule, schedule.getRequestCode(), schedule.getTime());
											in_d.cancel();
											loadPatientMasterList();
											loadPatientList(1);
											Toast.makeText(getActivity(), "Patient's Status Changed!", Toast.LENGTH_SHORT).show();
										
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
								in_d.cancel();
							}
						});
						
						in_d.show();
				}
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
	
	private void updateRounds(final Patients p)
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
		
		c.setTimeInMillis(p.getTime());
		h_name.setText(p.getHosp_name());
		h_room.setText(p.getHosp_room());
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

						Time time = new Time();
						time.hour = mHour;
						time.minute = mMin;
						time.second = 0;
						timeInMills = time.toMillis(false);
						
						Patients patients = p;
						
						patients.setHosp_name(h_name.getText().toString());
						patients.setHosp_room(h_room.getText().toString());
						
						patients.setLocation(h_name.getText().toString()+" - "+h_room.getText().toString());
						patients.setTime(timeInMills);
						
						Toast.makeText(getActivity(), "Rounds Schedule Updated!", Toast.LENGTH_SHORT).show();
						help.update(patients, Helper.UPDATE_SCHEDULE);
						//Helper.scheduleRepeatingAlarm(getActivity(), patients, patients.getRequestCode(), patients.getTime());
						loadPatientMasterList();
						loadPatientList(1);
						in_d.cancel();
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
	
	private void editPatients(final Patients patient)
	{
		final Dialog d = new Dialog(getActivity());
		d.setTitle("Update Entry");
		d.setContentView(R.layout.input_patient_entry);
			TextView header = (TextView) d.findViewById(R.id.header);
			header.setText("Update Contact Entry");
			final TextView first_name = (TextView) d.findViewById(R.id.fname);
			final TextView middle_name = (TextView) d.findViewById(R.id.mi);
			final TextView last_name = (TextView) d.findViewById(R.id.lname);
			final TextView address = (TextView) d.findViewById(R.id.addr);
			final TextView age = (TextView) d.findViewById(R.id.age);
			final TextView medical_history = (TextView) d.findViewById(R.id.med_hist);
			RadioGroup status_group = (RadioGroup) d.findViewById(R.id.status);
			status_group.setVisibility(RadioGroup.GONE);

			save = (Button) d.findViewById(R.id.add);
			Button cancel = (Button) d.findViewById(R.id.cancel);

			save.setEnabled(false);
			
			save.setText("Update");
			first_name.setText(patient.getFname());
			middle_name.setText(patient.getMi());
			last_name.setText(patient.getLname());
			address.setText(patient.getAddr());
			age.setText(Integer.toString(patient.getAge()));
			medical_history.setText(patient.getMed_history());
			
			first_name.addTextChangedListener(this);
			middle_name.addTextChangedListener(this);
			last_name.addTextChangedListener(this);
			address.addTextChangedListener(this);
			age.addTextChangedListener(this);
			medical_history.addTextChangedListener(this);
			
			save.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
					build.setMessage("Are all edits Correct?");	
			    	build.setCancelable(false);
					build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							Patients p = patient;
							p.setFname(first_name.getText().toString());
							p.setMi(middle_name.getText().toString());
							p.setLname(last_name.getText().toString());
							p.setAddr(address.getText().toString());
							p.setAge(Integer.parseInt(age.getText().toString()));
							p.setMed_history(medical_history.getText().toString());
							help.update(p,Helper.NORMAL);
							d.cancel();
							Toast.makeText(getActivity(), "Patient Details Updated!", Toast.LENGTH_SHORT).show();
							loadPatientMasterList();
							loadPatientList(patient.getPat_status());
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
			cancel.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					d.cancel();
				}
			});
			
			
		d.show();
	}
	
	private void deletePatients(final Patients p)
	{
		AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
    	switch (p.getPat_status()) {
		case 1:
			build.setMessage("Do you really want to Delete this Entry together with its Associated Data?");
			break;
		default:
			build.setMessage("Do you really want to Delete this Entry?");
			break;
		}
			
    	build.setCancelable(false);
		build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				int status = p.getPat_status();
					if(status == 1)
					{
						Schedule s = new Schedule();
						s.setSchedule_id(p.getSchedule_id());
						//Helper.cancelRepeatingAlarm(getActivity(), p.getRequestCode());
						help.delete(s);
						help.delete(p);
					}
					else
					{
						help.delete(p);
					}
				Toast.makeText(getActivity(), "Entry Deleted!", Toast.LENGTH_SHORT).show();
				loadPatientMasterList();
				loadPatientList(status);
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
	
	private void loadPatientMasterList()
	{
		master_list = new ArrayList<Patients>();
		master_list = convertListToPatients(help.getData(this,null));
	}
	
	private void loadPatientList(int status)
	{

		ArrayList<Patients> filtered = new ArrayList<Patients>();
		filtered.clear();
		filtered = filterPatientList(master_list, status);
		sortPatients(filtered);
		PatientAdapter patientListAdapter = new PatientAdapter(getActivity(),R.layout.patient_list_item_fragment, filtered);
		//getListView().addHeaderView(setHeader(status));
		setListAdapter(patientListAdapter);
		
		getListView().setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Patients patients = (Patients) getListAdapter().getItem(arg2);
				viewPatient(patients);
			}
		});
		
		registerForContextMenu(getListView());
	}
	
	private ArrayList<Patients> convertListToPatients(ArrayList<Object> o)
	{
		ArrayList<Patients> pList = new ArrayList<Patients>();
		Iterator<Object> i = o.iterator();
		Patients p;
		Object obj_item;
		while(i.hasNext())
		{
			p = new Patients();
			obj_item = i.next();
			if(obj_item instanceof Patients)
			{
				p = (Patients) obj_item;
				pList.add(p);
			}
		}
		
		for(Patients item: pList)
			Log.d("DataCheck", item.toString());
		
		Log.d("DataCheck", "Number of Records Retrieve: "+pList.size());
		return pList;
	}
	
	private void sortPatients(ArrayList<Patients> p)
	{
		Collections.sort(p, new Comparator<Patients>() {
		   public int compare(Patients o1, Patients o2) {
		      return o1.getLname().compareToIgnoreCase(o2.getLname());
		   }
		});	
	}
	
	private ArrayList<Patients> filterPatientList(ArrayList<Patients> list, int status)
	{
		ArrayList<Patients> new_list = new ArrayList<Patients>();
		Iterator<Patients> i = list.iterator();
		
			while(i.hasNext())
			{
				Patients p = i.next();
				if(p.getPat_status() == status)
					new_list.add(p);
			}
			
		return status != 0 ? new_list : list;
	}
	
	private View setHeader(int status)
	{
		View header = getActivity().getLayoutInflater().inflate(R.layout.list_header, null);
		TextView head = (TextView) header.findViewById(R.id.list_label);
		
			switch (status) {
			case 1:
				head.setText("In-Patients");
				break;
				
			case 2:
				head.setText("Out-Patients");
				break;

			default:
				head.setText("All-Patients");
				break;
			}
		return header;
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