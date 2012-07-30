package com.organizer.medical.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.organizer.medical.others.ContactAdapter;
import com.organizer.medical.others.Contacts;
import com.organizer.medical.others.CustomAdapter;
import com.organizer.medical.others.Patients;

import android.app.Activity;
import android.app.Dialog;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

public class ContactActivity extends Activity {
	private EditText fn; 
	private EditText ln;
	private EditText addr;
	private EditText numbr;	 
	private EditText spec;
	private Button add;
	private Dialog d;
	private ArrayList<Contacts> contactList;
	final MedicalHelper dbHelper = new MedicalHelper(this);
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ImageButton imb = (ImageButton) findViewById(R.id.add_con);
        loadList(0);
        imb.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 d = new Dialog(ContactActivity.this);
				d.setContentView(R.layout.add_contact);
				d.setTitle("Doctor's Profile");
				
				addr = (EditText) d.findViewById(R.id.Addr_c);
				numbr = (EditText) d.findViewById(R.id.number_c);
				fn = (EditText) d.findViewById(R.id.fname_c);
				ln = (EditText) d.findViewById(R.id.lname_c);
				spec = (EditText) d.findViewById(R.id.spec);
				
				add = (Button) d.findViewById(R.id.add_Con_info);
				
				add.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Contacts p = new Contacts();
						p.setAddr(addr.getText().toString());
						p.setSpec(spec.getText().toString());
						p.setFname(fn.getText().toString());
						p.setLname(ln.getText().toString());
						p.setNum(Integer.parseInt(numbr.getText().toString()));
						
						
						
					
							dbHelper.C_insertIntoDatabase("Contacts", p);
							
					
						dbHelper.close();
						Log.d("VALUES", p.toString());
						loadList(0);
						d.cancel();

					}
				});

				
				d.show();
			}
		});
    }

	public void loadList(int pa_status){
    	ListView list_view_con = (ListView) findViewById(R.id.list_con);
    	Cursor c;
        contactList = new ArrayList<Contacts>();    
        	try {
	        	 
        		dbHelper.openDataBase();
	     
	     	}catch(SQLException sqle){
	     
	     		throw sqle;
	     
	     	}
        			 c = dbHelper.retrieveAllData("Contacts");
        			 c.moveToFirst();
	            
	        	while(!c.isAfterLast())
	        	{
	        		Contacts p = new Contacts();
	        		p.setAddr(c.getString(c.getColumnIndex("Address")));
	        		p.setNum(c.getInt(c.getColumnIndex("C_number")));
	        		p.setFname(c.getString(c.getColumnIndex("Fname")));
	        		p.setLname(c.getString(c.getColumnIndex("Lname")));
	        		p.setSpec(c.getString(c.getColumnIndex("Specialty")));
	        		contactList.add(p);
	        		
	        		c.moveToNext();
	        	}
	     
        	Log.d("rowcount",Integer.toString(c.getCount()));
	        dbHelper.close();
	    
	    Collections.sort(contactList, new Comparator<Contacts>() {
	        public int compare(Contacts o1, Contacts o2) {
	           return o1.getLname().compareToIgnoreCase(o2.getLname());
	        }

		});
	    
	    for(Contacts p: contactList)
    	{
    		Log.d("LISTCONS",p.toString());
    	}
	    
	    ContactAdapter listAdapter = new ContactAdapter(this, R.layout.contact_list_tem, contactList);
	    list_view_con.setAdapter(listAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contact, menu);
        return true;
    }

    
}
