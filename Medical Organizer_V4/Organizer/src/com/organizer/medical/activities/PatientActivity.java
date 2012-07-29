package com.organizer.medical.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.organizer.medical.others.CustomAdapter;
import com.organizer.medical.others.Patients;
import android.os.Bundle;
import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.support.v4.app.NavUtils;

public class PatientActivity extends Activity {
	private EditText fn;
	private EditText mi;
	private EditText ln;
	private EditText addr;
	private EditText age;	
	private RadioButton status_in;
	private RadioButton status_out;
	private EditText history;
	private Button add;
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
				dbHelper.openDataBase();
				d.setContentView(R.layout.add_patient);
				d.setTitle("Patient Info");
				
				fn = (EditText) d.findViewById(R.id.fname);
				mi = (EditText) d.findViewById(R.id.mi);
				ln = (EditText) d.findViewById(R.id.lname);
				addr = (EditText) d.findViewById(R.id.addr);
				age = (EditText) d.findViewById(R.id.age);
				

				status_in = (RadioButton) d.findViewById(R.id.in);
				status_out = (RadioButton) d.findViewById(R.id.out);
				history = (EditText) d.findViewById(R.id.med_hist);
				
				add = (Button) d.findViewById(R.id.add_pat_info);
				
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
						
						try {
							dbHelper.insertIntoDatabase("Patients", p);
							if(status_in.isChecked())
								loadList(1);
							
							if(status_out.isChecked())
								loadList(2);
						} catch (SQLException e) {
							e.printStackTrace();
						}
						dbHelper.close();
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
        patientList = new ArrayList<Patients>();    
        	try {
	        	 
        		dbHelper.openDataBase();
	     
	     	}catch(SQLException sqle){
	     
	     		throw sqle;
	     
	     	}
        		if(pa_status == 0)
        			 c = dbHelper.retrieveAllData("Patients");
        		else
	        		 c = dbHelper.retrieveAllDataWhere("status", Integer.toString(pa_status));
	        	
	        	c.moveToFirst();
	            
	        	while(!c.isAfterLast())
	        	{
	        		Patients p = new Patients();
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
