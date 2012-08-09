package com.organizer.medical.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import com.organizer.medical.others.ContactAdapter;
import com.organizer.medical.others.Contacts;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ContactActivity extends Activity {
	private EditText fn; 
	private EditText ln;
	private EditText addr;
	private EditText numbr;	 
	private EditText spec;
	private Button add;
	private Dialog d;
	private ArrayList<Contacts> contactList;
	private ImageButton prevButton=null;
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
				d.setContentView(R.layout.add_update_contact);
				d.setTitle("Doctor's Profile");
				
				addr = (EditText) d.findViewById(R.id.address_ca);
				numbr = (EditText) d.findViewById(R.id.number_ca);
				fn = (EditText) d.findViewById(R.id.fname_ca);
				ln = (EditText) d.findViewById(R.id.lname_ca);
				spec = (EditText) d.findViewById(R.id.spec_ca);
				
				add = (Button) d.findViewById(R.id.add_edit_con_info);
				
				add.setOnClickListener(new OnClickListener() {
					
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Contacts p = new Contacts();
						p.setAddr(addr.getText().toString());
						p.setSpec(spec.getText().toString());
						p.setFname(fn.getText().toString());
						p.setLname(ln.getText().toString());
						p.setNum(Integer.parseInt(numbr.getText().toString()));
						
						

						insertNewPatient(p);
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
	        		Log.d("contact id",Integer.toString(c.getInt(c.getColumnIndex("_id"))));
	        		p.setC_id(c.getInt(c.getColumnIndex("_id")));
	        		p.setAddr(c.getString(c.getColumnIndex("Address")));
	        		p.setNum(c.getInt(c.getColumnIndex("C_number")));
	        		p.setFname(c.getString(c.getColumnIndex("Fname")));
	        		p.setLname(c.getString(c.getColumnIndex("Lname")));
	        		p.setSpec(c.getString(c.getColumnIndex("Specialty")));
	        		contactList.add(p);
	        		
	        		c.moveToNext();
	        	}
	        	dbHelper.close();
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
	    
	    //Delete contact
	    
	    list_view_con.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View v,
					int arg2, long arg3) {
				
				FrameLayout fl = (FrameLayout) v.findViewById(R.id.list_item_id);
				ImageButton image_button = (ImageButton) fl.findViewById(R.id.delete);
				
				final TextView hidden_id = (TextView) v.findViewById(R.id.con_id);
				
				if(prevButton != null)
					prevButton.setVisibility(FrameLayout.GONE);
				
				prevButton = image_button;
				
				image_button.setVisibility(FrameLayout.VISIBLE);
				
					image_button.setOnClickListener(new OnClickListener() {
						
						public void onClick(View v) {
							
							dbHelper.deleteRecordById("Contacts", Integer.parseInt(hidden_id.getText().toString()));
							
								loadList(0);
							Toast toast = Toast.makeText(ContactActivity.this, "DELETE!", 5000);
							toast.show();
						}
					});
				
				return false;
			}	
	    });
   
	    //edit contacts
	    list_view_con.setOnItemClickListener(new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View v, int arg2,
				long arg3) {
			//TextView fullname = (TextView) v.findViewById(R.id.full_name);
			
		
			
			TextView id = (TextView) v.findViewById(R.id.con_id);
			final Contacts con = new Contacts();
			Cursor c = dbHelper.retrieveSpecificId("Contacts",Integer.parseInt(id.getText().toString()));
			c.moveToFirst();

			con.setC_id(c.getInt(c.getColumnIndex("_id")));
			con.setFname(c.getString(c.getColumnIndex("Fname")));
			con.setLname(c.getString(c.getColumnIndex("Lname")));
			con.setAddr(c.getString(c.getColumnIndex("Address")));
			con.setNum(c.getInt(c.getColumnIndex("C_number")));
			con.setSpec(c.getString(c.getColumnIndex("Specialty")));	

			dbHelper.close();
			
			final Dialog d = new Dialog(ContactActivity.this);
			d.setContentView(R.layout.add_update_contact);
			d.setTitle("Update Info");
			
			final TextView fName = (TextView) d.findViewById(R.id.fname_ca);
			final TextView lName = (TextView) d.findViewById(R.id.lname_ca);
			final TextView address = (TextView) d.findViewById(R.id.address_ca);
			final TextView num = (TextView) d.findViewById(R.id.number_ca);
			final TextView specs = (TextView) d.findViewById(R.id.spec_ca);
			RelativeLayout rv_toggle = (RelativeLayout) d.findViewById(R.id.toggle);
			
			rv_toggle.setVisibility(RelativeLayout.VISIBLE);
			ToggleButton toggle = (ToggleButton) d.findViewById(R.id.update_edit);
			
			final Button update_edit = (Button) d.findViewById(R.id.add_edit_con_info);
			
			update_edit.setText("Update/Edit Doctor's Info");
			fName.setText(con.getFname());
			lName.setText(con.getLname());
			address.setText(con.getAddr());
			num.setText(Integer.toString(con.getNum()));
			specs.setText(con.getSpec());
			
			
			fName.setEnabled(false);
			lName.setEnabled(false);
			address.setEnabled(false);
			num.setEnabled(false);
			specs.setEnabled(false);
			update_edit.setEnabled(false);
			
			toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					// TODO Auto-generated method stub
					if(isChecked)
					{
						update_edit.setEnabled(true);
						fName.setEnabled(true);
						lName.setEnabled(true);
						address.setEnabled(true);
						num.setEnabled(true);
						specs.setEnabled(true);		

					}
					else
					{
						fName.setEnabled(false);
						lName.setEnabled(false);
						address.setEnabled(false);
						num.setEnabled(false);
						specs.setEnabled(false);
						update_edit.setEnabled(false);
	
					}
				}
			});

			update_edit.setOnClickListener(new OnClickListener() {
				
				public void onClick(View v) {
					//Toast toast = Toast.makeText(PatientActivity.this, "EDITED KUNO!", 5000);
					//toast.show();
					
					con.setFname(fName.getText().toString());
					con.setLname(lName.getText().toString());
					con.setAddr(address.getText().toString());
					con.setNum(Integer.parseInt(num.getText().toString()));
					con.setSpec(specs.getText().toString());
					
					
					

					updateInformation(con);
					loadList(0);
					
					
					d.cancel();
				}
			});
			
			d.show();
			
			//Toast toast = Toast.makeText(PatientActivity.this, "ID: "+c.getInt(c.getColumnIndex("_id"))+" Full Name: "+c.getString(c.getColumnIndex("address")), 5000);
			//toast.show();

		 }  
	   });
	    

	    
    }

    public void updateInformation(Contacts c){

    	ContentValues valuesToUpdate = new ContentValues();
    	valuesToUpdate.put("Address", c.getAddr());
    	valuesToUpdate.put("C_number", c.getNum());
    	valuesToUpdate.put("Fname", c.getFname());
    	valuesToUpdate.put("Lname", c.getLname());
    	valuesToUpdate.put("Specialty", c.getSpec());
    	dbHelper.updateSpecificId(valuesToUpdate, "Contacts", c.getC_id());
    }

    public void insertNewPatient(Contacts p){
    	ContentValues valuesToinsert = new ContentValues();
    	valuesToinsert.put("Address", p.getAddr());
    	valuesToinsert.put("C_number", p.getNum());
    	valuesToinsert.put("Fname", p.getFname());
    	valuesToinsert.put("Lname", p.getLname());
    	valuesToinsert.put("Specialty", p.getSpec());
    	dbHelper.insertIntoDatabase("Contacts", valuesToinsert);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contact, menu);
        return true;
    }

}
