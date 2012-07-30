package com.organizer.medical.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.organizer.medical.others.CustomAdapter;
import com.organizer.medical.others.Patients;

public class PatientActivity extends Activity {
	private final static String TABLE_NAME = "Patients";
	private static int flag;
	private EditText fn;
	private EditText mi;
	private EditText ln;
	private EditText addr;
	private EditText age;	
	private RadioButton status_in;
	private RadioButton status_out;
	private EditText history;
	private Button add;
	private ImageButton prevButton=null;
	private ArrayList<Patients> patientList;
	final MedicalHelper dbHelper = new MedicalHelper(this);
	private TextView cat;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        cat = (TextView) findViewById(R.id.category);
        cat.setText("All");
        ImageButton imb = (ImageButton) findViewById(R.id.add_pat);
        loadList(0);
       
        imb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog d = new Dialog(PatientActivity.this);
				d.setContentView(R.layout.add_update_patient);
				d.setTitle("Patient Info");
				
				fn = (EditText) d.findViewById(R.id.fname);
				mi = (EditText) d.findViewById(R.id.mi);
				ln = (EditText) d.findViewById(R.id.lname);
				addr = (EditText) d.findViewById(R.id.addr);
				age = (EditText) d.findViewById(R.id.age);
				

				status_in = (RadioButton) d.findViewById(R.id.in);
				status_out = (RadioButton) d.findViewById(R.id.out);
				history = (EditText) d.findViewById(R.id.med_hist);
				
				add = (Button) d.findViewById(R.id.add_edit_pat_info);
				
				add.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Patients p = new Patients();
						p.setFname(fn.getText().toString());
						p.setMi(mi.getText().toString());
						p.setLname(ln.getText().toString());
						p.setAddr(addr.getText().toString());
						p.setMed_history(history.getText().toString());
						p.setAge(Integer.parseInt(age.getText().toString()));
						
						if(status_in.isChecked())
							p.setPat_status(1);
						
						if(status_out.isChecked())
							p.setPat_status(2);
						
						insertNewPatient(p);
							
							if(status_in.isChecked())
									loadList(1);
								
							if(status_out.isChecked())
									loadList(2);
						
						
						Log.d("VALUES", p.toString());
						
						d.cancel();

					}
				});
				
				
				d.show();
			}
		});
    }
    
    public void loadList(int pa_status){
    	ListView list_view_pat = (ListView) findViewById(R.id.list_pat);
    	Cursor c;
    	flag = pa_status;
        patientList = new ArrayList<Patients>();    
        	
        		if(pa_status == 0)
        			 c = dbHelper.retrieveAllData(TABLE_NAME);
        		else
	        		 c = dbHelper.retrieveAllDataWhere(TABLE_NAME,"status", Integer.toString(pa_status));
	        	
	        	c.moveToFirst();
	            
	        	while(!c.isAfterLast())
	        	{
	        		Patients p = new Patients();
	        		Log.d("patient id",Integer.toString(c.getInt(c.getColumnIndex("_id"))));
	        		p.setId(c.getInt(c.getColumnIndex("_id")));
	        		p.setFname(c.getString(c.getColumnIndex("firstname")));
	        		p.setMi(c.getString(c.getColumnIndex("middleinitial")));
	        		p.setLname(c.getString(c.getColumnIndex("lastname")));
	        		p.setAddr(c.getString(c.getColumnIndex("address")));
	        		p.setAge(c.getInt(c.getColumnIndex("age")));
	        		p.setMed_history(c.getString(c.getColumnIndex("med_history")));
	        		p.setPat_status(c.getInt(c.getColumnIndex("status")));
	        		patientList.add(p);
	        		
	        		c.moveToNext();
	        	}
	        	dbHelper.close();
        	Log.d("rowcount",Integer.toString(c.getCount()));
	        dbHelper.close();
	    
	    Collections.sort(patientList, new Comparator<Patients>() {
	        public int compare(Patients o1, Patients o2) {
	           return o1.getLname().compareToIgnoreCase(o2.getLname());
	        }
		});
	    
	    for(Patients p: patientList)
    	{
    		Log.d("LISTVALS",p.toString());
    	}
	    
	    CustomAdapter listAdapter = new CustomAdapter(this, R.layout.patient_list_tem, patientList);
	    list_view_pat.setAdapter(listAdapter);  
	    
	    list_view_pat.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) {
				
				FrameLayout fl = (FrameLayout) v.findViewById(R.id.list_item_id);
				ImageButton image_button = (ImageButton) fl.findViewById(R.id.delete);
				
				final TextView hidden_id = (TextView) v.findViewById(R.id.pat_id);
				
				if(prevButton != null)
					prevButton.setVisibility(FrameLayout.GONE);
				
				prevButton = image_button;
				
				image_button.setVisibility(FrameLayout.VISIBLE);
				
					image_button.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							
							dbHelper.deleteRecordById(TABLE_NAME, Integer.parseInt(hidden_id.getText().toString()));
							if(flag == 1)
								loadList(flag);
							else if(flag == 2)
								loadList(flag);
							else if(flag == 0)
								loadList(flag);
							Toast toast = Toast.makeText(PatientActivity.this, "DELETE!", 5000);
							toast.show();
						}
					});
				
				return false;
			}	
	    });
   
	    list_view_pat.setOnItemClickListener(new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			//TextView fullname = (TextView) v.findViewById(R.id.full_name);
			
		
			
			TextView id = (TextView) v.findViewById(R.id.pat_id);
			final Patients pat = new Patients();
			Cursor c = dbHelper.retrieveSpecificId(TABLE_NAME,Integer.parseInt(id.getText().toString()));
			c.moveToFirst();

			pat.setId(c.getInt(c.getColumnIndex("_id")));
			pat.setFname(c.getString(c.getColumnIndex("firstname")));
			pat.setMi(c.getString(c.getColumnIndex("middleinitial")));
			pat.setLname(c.getString(c.getColumnIndex("lastname")));
			pat.setAddr(c.getString(c.getColumnIndex("address")));
			pat.setAge(c.getInt(c.getColumnIndex("age")));
			pat.setMed_history(c.getString(c.getColumnIndex("med_history")));	
			pat.setPat_status(c.getInt(c.getColumnIndex("status")));

			dbHelper.close();
			
			final Dialog d = new Dialog(PatientActivity.this);
			d.setContentView(R.layout.add_update_patient);
			d.setTitle("Update Info");
			final TextView fName = (TextView) d.findViewById(R.id.fname);
			final TextView mInit = (TextView) d.findViewById(R.id.mi);
			final TextView lName = (TextView) d.findViewById(R.id.lname);
			final TextView address = (TextView) d.findViewById(R.id.addr);
			final TextView age = (TextView) d.findViewById(R.id.age);
			final TextView med_hist = (TextView) d.findViewById(R.id.med_hist);
			final RadioButton stat_in = (RadioButton) d.findViewById(R.id.in);
			final RadioButton stat_out = (RadioButton) d.findViewById(R.id.out);
			RelativeLayout rv_toggle = (RelativeLayout) d.findViewById(R.id.toggle);
			
			rv_toggle.setVisibility(RelativeLayout.VISIBLE);
			ToggleButton toggle = (ToggleButton) d.findViewById(R.id.update_edit);
			
			final Button update_edit = (Button) d.findViewById(R.id.add_edit_pat_info);
			
			update_edit.setText("Update/Edit Patient Info");
			fName.setText(pat.getFname());
			mInit.setText(pat.getMi());
			lName.setText(pat.getLname());
			address.setText(pat.getAddr());
			age.setText(Integer.toString(pat.getAge()));
			med_hist.setText(pat.getMed_history());
			
			if(pat.getPat_status() == 1)
				stat_in.setChecked(true);
			else
				stat_out .setChecked(true);
			
			fName.setEnabled(false);
			mInit.setEnabled(false);
			lName.setEnabled(false);
			address.setEnabled(false);
			age.setEnabled(false);
			med_hist.setEnabled(false);
			stat_in.setEnabled(false);
			stat_out.setEnabled(false);
			update_edit.setEnabled(false);
			
			toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked)
					{
						update_edit.setEnabled(true);
						fName.setEnabled(true);
						mInit.setEnabled(true);
						lName.setEnabled(true);
						address.setEnabled(true);
						age.setEnabled(true);
						med_hist.setEnabled(true);
						stat_in.setEnabled(true);
						stat_out.setEnabled(true);		

					}
					else
					{
						fName.setEnabled(false);
						mInit.setEnabled(false);
						lName.setEnabled(false);
						address.setEnabled(false);
						age.setEnabled(false);
						med_hist.setEnabled(false);
						stat_in.setEnabled(false);
						stat_out.setEnabled(false);
						update_edit.setEnabled(false);
	
					}
				}
			});

			update_edit.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					//Toast toast = Toast.makeText(PatientActivity.this, "EDITED KUNO!", 5000);
					//toast.show();
					
					pat.setFname(fName.getText().toString());
					pat.setMi(mInit.getText().toString());
					pat.setLname(lName.getText().toString());
					pat.setAddr(address.getText().toString());
					pat.setAge(Integer.parseInt(age.getText().toString()));
					pat.setMed_history(med_hist.getText().toString());
					
					if(stat_in.isChecked())
						pat.setPat_status(1);
					
					if(stat_out.isChecked())
						pat.setPat_status(2);
					

					updateInformation(pat);
					
					if(pat.getPat_status() == 1)
						loadList(pat.getPat_status());
					else if(pat.getPat_status() == 2)
						loadList(pat.getPat_status());
					
					d.cancel();
				}
			});
			
			d.show();
			
			//Toast toast = Toast.makeText(PatientActivity.this, "ID: "+c.getInt(c.getColumnIndex("_id"))+" Full Name: "+c.getString(c.getColumnIndex("address")), 5000);
			//toast.show();

		 }  
	   });
	    

    }
    
    public void updateInformation(Patients p){

    	ContentValues valuesToUpdate = new ContentValues();
    	valuesToUpdate.put("firstname", p.getFname());
    	valuesToUpdate.put("middleinitial", p.getMi());
    	valuesToUpdate.put("lastname", p.getLname());
    	valuesToUpdate.put("address", p.getAddr());
    	valuesToUpdate.put("age", p.getAge());
    	valuesToUpdate.put("med_history", p.getMed_history());
    	valuesToUpdate.put("status", p.getPat_status());
    	dbHelper.updateSpecificId(valuesToUpdate, TABLE_NAME, p.getId());
    }
    
    public void insertNewPatient(Patients p){
    	ContentValues valuesToinsert = new ContentValues();
    	valuesToinsert.put("firstname", p.getFname());
    	valuesToinsert.put("middleinitial", p.getMi());
    	valuesToinsert.put("lastname", p.getLname());
    	valuesToinsert.put("address", p.getAddr());
    	valuesToinsert.put("age", p.getAge());
    	valuesToinsert.put("med_history", p.getMed_history());
    	valuesToinsert.put("status", p.getPat_status());
    	dbHelper.insertIntoDatabase(TABLE_NAME, valuesToinsert);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater menuInflater = getMenuInflater();
    	menuInflater.inflate(R.menu.activity_patient, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	
    	cat = (TextView) findViewById(R.id.category);
    	if(item.getItemId() == R.id.in_pat)
    	{
    		Log.d("options", "in-patients here!");
    		loadList(1);
    	
    		cat.setText("In-Patients");
    	}
    	else if(item.getItemId() == R.id.out_pat)
    	{
    		Log.d("options", "out-patients here!");
    		loadList(2);
    
    		cat.setText("Out-Patients");
    	}
    	else if(item.getItemId() == R.id.all_pat)
    	{
    		Log.d("options", "all-patients here!");
    		loadList(0);
    		
    		cat.setText("All");
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    
}
