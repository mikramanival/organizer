package com.medical.organizer;

import com.medical.organizer.fragments.ContactListFragment;
import com.medical.organizer.fragments.PatientListFragment;
import com.medical.organizer.fragments.ScheduleFragment;
import com.medical.organizer.others.MedicalDatabaseHelper;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ActionBar.Tab;
import android.widget.Toast;

public class Main extends Activity {
	
	private int status;
	private String date;
	private long dateInMills;
	private String type;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        MedicalDatabaseHelper dbHelper = new MedicalDatabaseHelper(this);
        dateInMills = System.currentTimeMillis();
			try {
				dbHelper.createDataBase();
				dbHelper.close();
			} catch (Exception e) {
				throw new Error("Unable to create database");
			}
			
        
        ActionBar acBar = getActionBar();
        acBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.Tab sched_tab = acBar.newTab().setText("Schedule");
        ActionBar.Tab patient_tab = acBar.newTab().setText("Patients");
        ActionBar.Tab contact_tab = acBar.newTab().setText("Contact");

        sched_tab.setTabListener(new MyTabListener(this, new ScheduleFragment()));
        patient_tab.setTabListener(new MyTabListener(this, new PatientListFragment()));
        contact_tab.setTabListener(new MyTabListener(this, new ContactListFragment()));
        
        acBar.addTab(sched_tab);
        acBar.addTab(patient_tab);
        acBar.addTab(contact_tab);
    }
    
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	super.onBackPressed();
    	getActionBar().show();
		FragmentManager fm = getFragmentManager();
		fm.popBackStack();
    }


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public String getDate() {
		return date;
	}


	public void setDate(String date) {
		this.date = date;
	}



	public long getDateInMills() {
		return dateInMills;
	}


	public void setDateInMills(long dateInMills) {
		this.dateInMills = dateInMills;
	}

	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

    
    
}

class MyTabListener implements ActionBar.TabListener{
	private Fragment gFrag;
	private Activity gAct;
	
	public MyTabListener(Activity act, Fragment frag){
		this.gFrag = frag;
		this.gAct = act;
	}
	
	
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		Toast.makeText(gAct, "Reselected!", Toast.LENGTH_LONG).show();
	}

	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		ft.replace(R.id.fragment_container, gFrag);
	}

	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		ft.remove(gFrag);
		
	}
	
}
